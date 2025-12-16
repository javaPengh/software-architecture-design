package org.zzu.membership.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.lang.util.ByteSource;
import org.apache.shiro.authc.UsernamePasswordToken;

public class SimpleRealm extends AuthorizingRealm {

    private final Map<String, String> userStore = new HashMap<>();
    private final Map<String, Set<String>> roleStore = new HashMap<>();

    public SimpleRealm() {
        userStore.put("admin", "admin123");
        userStore.put("user", "user123");

        roleStore.put("admin", Set.of("admin"));
        roleStore.put("user", Set.of("member"));
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        Set<String> roles = roleStore.getOrDefault(username, new HashSet<>());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (!(token instanceof UsernamePasswordToken upt)) {
            throw new AuthenticationException("Unsupported token type");
        }
        String username = upt.getUsername();
        String stored = userStore.get(username);
        if (stored == null) {
            throw new UnknownAccountException("Unknown user");
        }

        String provided = upt.getPassword() == null ? null : new String(upt.getPassword());
        if (provided == null || !stored.equals(provided)) {
            throw new IncorrectCredentialsException("Bad credentials");
        }

        return new SimpleAuthenticationInfo(username, stored, ByteSource.Util.bytes(username), getName());
    }
}

