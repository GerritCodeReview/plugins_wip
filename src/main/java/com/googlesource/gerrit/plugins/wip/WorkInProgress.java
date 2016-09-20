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

import java.io.IOException;

import com.google.gerrit.extensions.restapi.ResourceConflictException;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.webui.UiAction;
import com.google.gerrit.reviewdb.client.Change;
import com.google.gerrit.reviewdb.client.Change.Status;
import com.google.gerrit.reviewdb.client.PatchSet;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gerrit.server.git.BatchUpdate;
import com.google.gerrit.server.git.UpdateException;
import com.google.gerrit.server.index.change.ChangeIndexer;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;

class WorkInProgress extends BaseAction implements
    UiAction<RevisionResource>,
    RestModifyView<RevisionResource, BaseAction.Input> {

  @Inject
  WorkInProgress(Provider<ReviewDb> dbProvider,
      Provider<CurrentUser> userProvider,
      ChangeIndexer indexer,
      BatchUpdate.Factory batchUpdateFactory) {
    super(dbProvider, userProvider, indexer, batchUpdateFactory);
  }

  @Override
  public Object apply(RevisionResource rsrc, Input input)
      throws RestApiException, OrmException, IOException, UpdateException {
    Change change = rsrc.getChange();
    if (change.getStatus() != Status.NEW) {
      throw new ResourceConflictException("change is " + status(change));
    }

    if (!change.currentPatchSetId().equals(rsrc.getPatchSet().getId())) {
      throw new ResourceConflictException("not current patch set");
    }

    changeStatus(change, rsrc.getPatchSet().getId(), input, Status.NEW,
        Status.DRAFT);
    return Response.none();
  }

  @Override
  public Description getDescription(RevisionResource rsrc) {
    PatchSet.Id current = rsrc.getChange().currentPatchSetId();
    return new Description()
        .setLabel("WIP")
        .setTitle("Set Work In Progress")
        .setVisible(rsrc.getControl().isOwner()
           && rsrc.getChange().getStatus() == Status.NEW
           && rsrc.getPatchSet().getId().equals(current));
  }
}
