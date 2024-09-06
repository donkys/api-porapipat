package com.porapipat.porapipat_api.service.security;

import com.porapipat.porapipat_api.entity.ApiPermissionsEntity;
import com.porapipat.porapipat_api.entity.UsersEntity;
import com.porapipat.porapipat_api.repository.ApiPermissionsInterfaceRepository;
import com.porapipat.porapipat_api.repository.UsersInterfaceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Log4j2
public class SecurityService {

    private final ApiPermissionsInterfaceRepository apiPermissionsInterfaceRepository;
    private final UsersInterfaceRepository usersInterfaceRepository;

    public SecurityService(ApiPermissionsInterfaceRepository apiPermissionsInterfaceRepository, UsersInterfaceRepository usersInterfaceRepository) {
        this.apiPermissionsInterfaceRepository = apiPermissionsInterfaceRepository;
        this.usersInterfaceRepository = usersInterfaceRepository;
    }

    public boolean hasAccess(Authentication auth, String apiName, String permission) {
        String username = auth.getName();
        UsersEntity user = usersInterfaceRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (hasAdminRole(user)) {
            return true;
        }

        List<ApiPermissionsEntity> permissions = apiPermissionsInterfaceRepository.findByUsersByUserIdUsernameAndApiName(username, apiName);
        return permissions.stream().anyMatch(p -> p.getPermission().equals(permission));
    }

    private boolean hasAdminRole(UsersEntity user) {
        return user.getUserRolesById().stream()
                .anyMatch(userRole -> "ROLE_ADMIN".equals(userRole.getRolesByRoleId().getName()));
    }
}

//    public boolean hasAccess(Authentication auth, String apiName, String permission) {
//        String username = auth.getName();
//        List<ApiPermissionsEntity> permissions = apiPermissionsInterfaceRepository.findByUsersByUserIdUsernameAndApiName(username, apiName);
//        return permissions.stream().anyMatch(p -> p.getPermission().equals(permission));
//    }
//}
