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
package org.flowable.ui.common.security;

import com.suneee.eas.common.utils.ContextSupportUtil;
import com.suneee.platform.model.system.SysUser;
import org.flowable.idm.api.User;
import org.flowable.ui.common.model.RemoteUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for Spring Security.
 */
public class SecurityUtils {

    private static User assumeUser;

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentUserId() {
        Long userId= ContextSupportUtil.getCurrentUserId();
        if (userId==null){
            return null;
        }
        return String.valueOf(userId);
    }

    /**
     * @return the {@link User} object associated with the current logged in user.
     */
    public static User getCurrentUserObject() {
        if (assumeUser != null) {
            return assumeUser;
        }

        User user = null;
        FlowableAppUser appUser = getCurrentFlowableAppUser();
        if (appUser != null) {
            user = appUser.getUserObject();
        }
        return user;
    }

    public static FlowableAppUser getCurrentFlowableAppUser() {
        FlowableAppUser user = null;
        SysUser sysUser=ContextSupportUtil.getCurrentUser();
        RemoteUser remoteUser=new RemoteUser();
        String username=ContextSupportUtil.getUsername(sysUser);
        remoteUser.setFullName(sysUser.getFullname());
        remoteUser.setDisplayName(username);
        remoteUser.setEmail(sysUser.getEmail());
        remoteUser.setFirstName(username);
        remoteUser.setId(username);
        remoteUser.setLastName("");
        List<GrantedAuthority> authorityList=new ArrayList<>();
        user=new FlowableAppUser(remoteUser,remoteUser.getId(),authorityList);
        return user;
    }

    public static boolean currentUserHasCapability(String capability) {
        FlowableAppUser user = getCurrentFlowableAppUser();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()) {
            if (capability.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static void assumeUser(User user) {
        assumeUser = user;
    }

    public static void clearAssumeUser() {
        assumeUser = null;
    }

}
