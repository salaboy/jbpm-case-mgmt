/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.casemgmt.model;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.casemgmt.api.HumanTask;

/**
 *
 * @author salaboy
 */
public class HumanTaskImpl implements HumanTask {

    private static Long idGenerator = 0L;

    private Long id;
    private String name;
    private List<String> groups = new ArrayList<String>();
    private List<String> users = new ArrayList<String>();

    public HumanTaskImpl() {
        this.id = ++idGenerator;
    }

    public HumanTaskImpl(String name) {
        this();
        this.name = name;
    }

    public HumanTaskImpl(String name, List<String> users, List<String> groups) {
        this(name);
        this.groups = groups;
        this.users = users;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public List<String> getUsers() {
        return users;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

}
