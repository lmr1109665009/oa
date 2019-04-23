/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.flowable.ui.common.rest.idm.remote;

import java.util.ArrayList;
import java.util.List;

import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.SysUser;
import org.flowable.ui.common.model.GroupRepresentation;
import org.flowable.ui.common.model.RemoteGroup;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.NotFoundException;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class RemoteAccountResource {

    @Autowired
    private RemoteIdmService remoteIdmService;

    /**
     * GET /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/account", method = RequestMethod.GET, produces = "application/json")
    public UserRepresentation getAccount() {
        UserRepresentation userRepresentation = null;
        RemoteUser remoteUser=new RemoteUser();
        SysUser user= ContextSupportUtil.getCurrentUser();
        remoteUser.setFullName(user.getFullname());
        remoteUser.setDisplayName(ContextSupportUtil.getUsername(user));
        remoteUser.setEmail(user.getEmail());
        remoteUser.setFirstName(ContextSupportUtil.getUsername(user));
        remoteUser.setId(String.valueOf(user.getUserId()));
        remoteUser.setLastName("");
        List<String> privilegeList=new ArrayList<>();
        privilegeList.add("access-modeler");
        userRepresentation = new UserRepresentation(remoteUser);
        userRepresentation.setPrivileges(privilegeList);
        return userRepresentation;
    }

}
