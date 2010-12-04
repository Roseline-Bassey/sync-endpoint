/*
 * Copyright (C) 2010 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.opendatakit.aggregate.query.submission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opendatakit.aggregate.exception.ODKFormNotFoundException;
import org.opendatakit.aggregate.exception.ODKIncompleteSubmissionData;
import org.opendatakit.aggregate.form.Form;
import org.opendatakit.aggregate.submission.Submission;
import org.opendatakit.common.persistence.CommonFieldsBase;
import org.opendatakit.common.persistence.Datastore;
import org.opendatakit.common.persistence.Query;
import org.opendatakit.common.persistence.TopLevelDynamicBase;
import org.opendatakit.common.persistence.exception.ODKDatastoreException;
import org.opendatakit.common.security.User;

/**
 * 
 * @author wbrunette@gmail.com
 * @author mitchellsundt@gmail.com
 * 
 */
public class QueryByDateRange extends QueryBase {

  public QueryByDateRange(Form form, int maxFetchLimit, Date startDate, Date endDate, Datastore datastore, User user)
      throws ODKFormNotFoundException {
    super(form, maxFetchLimit, datastore, user);
   
    CommonFieldsBase tbl = form.getFormDefinition().getTopLevelGroup().getBackingObjectPrototype();
    
    query = ds.createQuery(tbl, user);
    query.addSort(tbl.creationDate, Query.Direction.ASCENDING);
    query.addFilter(tbl.creationDate, Query.FilterOperation.LESS_THAN, endDate);
    query.addFilter(tbl.creationDate, Query.FilterOperation.GREATER_THAN, startDate);
  }

  @Override
  public List<Submission> getResultSubmissions() throws ODKIncompleteSubmissionData,
      ODKDatastoreException {

    List<Submission> retrievedSubmissions = new ArrayList<Submission>();

    // retrieve submissions
    List<? extends CommonFieldsBase> submissionEntities = getSubmissionEntities();

    // create a row for each submission
    for (int count = 0; count < submissionEntities.size(); count++) {
    CommonFieldsBase subEntity = submissionEntities.get(count);
      retrievedSubmissions.add(new Submission((TopLevelDynamicBase) subEntity, form.getFormDefinition(), ds, user));
    }
    return retrievedSubmissions;
  }

}