// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.wip;

import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.reviewdb.client.PatchSet;
import com.google.gerrit.server.change.ChangesCollection;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gerrit.server.index.change.ChangeIndexer;
import com.google.gerrit.server.project.ProjectControl;
import com.google.gerrit.server.update.UpdateException;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.gerrit.sshd.commands.PatchSetParser;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CommandMetaData(name = "set", description = "Mark the change as WIP or Ready")
class SetCommand extends SshCommand {
  private static final Logger log = LoggerFactory.getLogger(SetCommand.class);

  private final Set<PatchSet> patchSets = new HashSet<>();

  @Argument(
    index = 0,
    required = true,
    multiValued = true,
    metaVar = "{COMMIT | CHANGE,PATCHSET}",
    usage = "list of commits or patch sets to review"
  )
  void addPatchSetId(String token) {
    try {
      PatchSet ps = psParser.parsePatchSet(token, projectControl, branch);
      patchSets.add(ps);
    } catch (UnloggedFailure e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    } catch (OrmException e) {
      throw new IllegalArgumentException("database error", e);
    }
  }

  @Option(
    name = "--project",
    aliases = "-p",
    usage = "project containing the specified patch set(s)"
  )
  private ProjectControl projectControl;

  @Option(name = "--branch", aliases = "-b", usage = "branch containing the specified patch set(s)")
  private String branch;

  @Option(
    name = "--message",
    aliases = "-m",
    usage = "cover message on change(s)",
    metaVar = "MESSAGE"
  )
  private String changeComment;

  @Option(name = "--wip", aliases = "-w", usage = "mark the specified change(s) as WIP")
  private boolean wipChange;

  @Option(
    name = "--readyChange",
    aliases = "-r",
    usage = "mark the specified change(s) as Ready for Review"
  )
  private boolean readyChange;

  @Inject private Provider<WorkInProgress> wipProvider;

  @Inject private Provider<ReadyForReview> readyProvider;

  @Inject ChangeIndexer indexer;

  @Inject ChangesCollection changes;

  @Inject PatchSetParser psParser;

  @Override
  protected void run() throws UnloggedFailure {
    if (wipChange && readyChange) {
      throw error("wip and ready options are mutually exclusive");
    }
    if (!wipChange && !readyChange) {
      throw error("wip or ready option must be specified");
    }

    boolean ok = true;
    for (PatchSet patchSet : patchSets) {
      try {
        mark(patchSet);
      } catch (RestApiException | IOException | OrmException | UpdateException e) {
        ok = false;
        writeError("fatal: internal server error while approving " + patchSet.getId() + "\n");
        log.error("internal error while approving " + patchSet.getId(), e);
      }
    }

    if (!ok) {
      throw new UnloggedFailure(1, "one or more mark operations failed");
    }
  }

  private void mark(PatchSet patchSet)
      throws ResourceConflictException, OrmException, IOException, RestApiException,
          UpdateException {
    RevisionResource rsrc =
        new RevisionResource(changes.parse(patchSet.getId().getParentKey()), patchSet);
    BaseAction.Input input = new BaseAction.Input();
    input.message = changeComment;
    if (wipChange) {
      wipProvider.get().apply(rsrc, input);
    } else {
      readyProvider.get().apply(rsrc, input);
    }
  }

  private void writeError(String msg) {
    try {
      err.write(msg.getBytes(ENC));
    } catch (IOException e) {
    }
  }

  private static UnloggedFailure error(String msg) {
    return new UnloggedFailure(1, msg);
  }
}
