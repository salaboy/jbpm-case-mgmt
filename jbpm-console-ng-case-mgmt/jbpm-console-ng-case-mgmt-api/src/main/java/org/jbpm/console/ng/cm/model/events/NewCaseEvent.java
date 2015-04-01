/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.cm.model.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewCaseEvent implements Serializable {
    
    private static final long serialVersionUID = -7547942104170821133L;
    
    private Long newCaseId;
    
    private String newCaseName;
    
    
    public NewCaseEvent(){
    }
    
    public NewCaseEvent(Long newCaseId, String newCaseName){
        this.newCaseId = newCaseId;
        this.newCaseName = newCaseName;
    }

    public Long getNewCaseId() {
        return newCaseId;
    }

    public String getNewCaseName() {
        return newCaseName;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newCaseId == null) ? 0 : newCaseId.hashCode());
        result = prime * result + ((newCaseName == null) ? 0 : newCaseName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NewCaseEvent other = (NewCaseEvent) obj;
        if (newCaseId == null) {
            if (other.newCaseId != null)
                return false;
        } else if (!newCaseId.equals(other.newCaseId))
            return false;
        if (newCaseName == null) {
            if (other.newCaseName != null)
                return false;
        } else if (!newCaseName.equals(other.newCaseName))
            return false;
        return true;
    }

    
  

}
