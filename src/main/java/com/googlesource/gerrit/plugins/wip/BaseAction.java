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

import com.google.common.base.Strings;
import com.google.gerrit.common.TimeUtil;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.reviewdb.client.Change;
import com.google.gerrit.reviewdb.client.Change.Status;
import com.google.gerrit.reviewdb.client.ChangeMessage;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.ChangeUtil;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.git.BatchUpdate;
import com.google.gerrit.server.git.UpdateException;
import com.google.gerrit.server.index.ChangeIndexer;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.util.Collections;

abstract class BaseAction {
  static class Input {
    String message;
  }

  private final Provider<ReviewDb> dbProvider;
  private final Provider<CurrentUser> userProvider;
  private final ChangeIndexer indexer;
  private final BatchUpdate.Factory batchUpdateFactory;

  @Inject
  BaseAction(Provider<ReviewDb> dbProvider,
      Provider<CurrentUser> userProvider,
      ChangeIndexer indexer,
      BatchUpdate.Factory batchUpdateFactory) {
    this.dbProvider = dbProvider;
    this.userProvider = userProvider;
    this.indexer = indexer;
    this.batchUpdateFactory = batchUpdateFactory;
  }

  protected void changeStatus(Change change, Input input, final Status from,
      final Status to) throws OrmException, RestApiException,
      IOException, UpdateException {
    ReviewDb db = dbProvider.get();
    Change.Id changeId = change.getId();
    db.changes().beginTransaction(changeId);
    try (BatchUpdate bu = batchUpdateFactory.create(
        db, change.getProject(), userProvider.get(), TimeUtil.nowTs())) {
      bu.addOp(change.getId(), new BatchUpdate.Op() {
        @Override
        public boolean updateChange(BatchUpdate.ChangeContext ctx) {
          Change change = ctx.getChange();
          if (change.getStatus() == from) {
            change.setStatus(to);
            ctx.saveChange();
            return true;
          }
          return false;
        }
      });
      bu.execute();

      db.changeMessages().insert(Collections.singleton(
          newMessage(input, change)));

      db.commit();
    } finally {
      db.rollback();
    }

    indexer.index(db, change);
  }

  private ChangeMessage newMessage(Input input,
      Change change) throws OrmException {
    StringBuilder buf = new StringBuilder(change.getStatus() == Status.DRAFT
        ? "Work In Progress"
        : "Ready For Review");

    String msg = Strings.nullToEmpty(input.message).trim();
    if (!msg.isEmpty()) {
      buf.append("\n\n");
      buf.append(msg);
    }

    ChangeMessage message = new ChangeMessage(
        new ChangeMessage.Key(
            change.getId(),
            ChangeUtil.messageUUID(dbProvider.get())),
        ((IdentifiedUser)userProvider.get()).getAccountId(),
        change.getLastUpdatedOn(),
        change.currentPatchSetId());
    message.setMessage(msg.toString());
    return message;
  }

  protected static String status(Change change) {
    return change != null
        ? change.getStatus().name().toLowerCase()
        : "deleted";
  }
}
