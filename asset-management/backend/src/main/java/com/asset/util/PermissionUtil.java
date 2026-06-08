package com.asset.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限工具类
 */
public final class PermissionUtil {

    private static final Map<String, List<String>> IMPLIED_PERMISSIONS = Map.ofEntries(
            Map.entry("acquisition:purchase", List.of(
                    "acquisition:purchase:create",
                    "acquisition:purchase:edit",
                    "acquisition:purchase:delete",
                    "acquisition:purchase:submit",
                    "acquisition:purchase:approve"
            )),
            Map.entry("acquisition:acceptance", List.of(
                    "acquisition:acceptance:create",
                    "acquisition:acceptance:edit",
                    "acquisition:acceptance:delete",
                    "acquisition:acceptance:submit",
                    "acquisition:acceptance:approve"
            )),
            Map.entry("acquisition:storage", List.of(
                    "acquisition:storage:create",
                    "acquisition:storage:edit",
                    "acquisition:storage:delete",
                    "acquisition:storage:confirm"
            )),
            Map.entry("management:requisition", List.of(
                    "management:requisition:create",
                    "management:requisition:edit",
                    "management:requisition:delete",
                    "management:requisition:submit",
                    "management:requisition:approve"
            )),
            Map.entry("management:transfer", List.of(
                    "management:transfer:create",
                    "management:transfer:edit",
                    "management:transfer:delete",
                    "management:transfer:approve",
                    "management:transfer:complete",
                    "management:transfer:cancel"
            )),
            Map.entry("management:borrow", List.of(
                    "management:borrow:create",
                    "management:borrow:edit",
                    "management:borrow:delete",
                    "management:borrow:approve",
                    "management:borrow:return",
                    "management:borrow:cancel",
                    "management:borrow:remind"
            )),
            Map.entry("archive:maintenance", List.of(
                    "archive:maintenance:create",
                    "archive:maintenance:edit",
                    "archive:maintenance:delete",
                    "archive:maintenance:approve",
                    "archive:maintenance:start",
                    "archive:maintenance:complete",
                    "archive:maintenance:cancel"
            )),
            Map.entry("disposal:scrap", List.of(
                    "disposal:scrap:create",
                    "disposal:scrap:edit",
                    "disposal:scrap:delete",
                    "disposal:scrap:approve"
            )),
            Map.entry("disposal:approval", List.of(
                    "disposal:approval:approve",
                    "disposal:approval:execute",
                    "disposal:approval:complete",
                    "disposal:approval:cancel"
            )),
            Map.entry("disposal:sale", List.of(
                    "disposal:sale:create",
                    "disposal:sale:edit",
                    "disposal:sale:delete",
                    "disposal:sale:approve",
                    "disposal:sale:execute",
                    "disposal:sale:complete",
                    "disposal:sale:cancel"
            )),
            Map.entry("inventory:plan", List.of(
                    "inventory:plan:create",
                    "inventory:plan:edit",
                    "inventory:plan:delete",
                    "inventory:plan:enable",
                    "inventory:plan:disable",
                    "inventory:plan:execute"
            )),
            Map.entry("inventory:task", List.of(
                    "inventory:task:create",
                    "inventory:task:edit",
                    "inventory:task:delete",
                    "inventory:task:start",
                    "inventory:task:complete",
                    "inventory:task:cancel"
            )),
            Map.entry("inventory:result", List.of(
                    "inventory:result:create",
                    "inventory:result:edit",
                    "inventory:result:delete",
                    "inventory:result:submit",
                    "inventory:result:review",
                    "inventory:result:process",
                    "inventory:result:import"
            )),
            Map.entry("report:statistics", List.of(
                    "report:statistics:export"
            )),
            Map.entry("report:depreciation", List.of(
                    "report:depreciation:export"
            )),
            Map.entry("report:disposal", List.of(
                    "report:disposal:export"
            )),
            Map.entry("basic:category", List.of(
                    "basic:category:create",
                    "basic:category:edit",
                    "basic:category:delete"
            )),
            Map.entry("basic:location", List.of(
                    "basic:location:create",
                    "basic:location:edit",
                    "basic:location:delete"
            )),
            Map.entry("basic:department", List.of(
                    "basic:department:create",
                    "basic:department:edit",
                    "basic:department:delete"
            )),
            Map.entry("system:user", List.of(
                    "system:user:create",
                    "system:user:edit",
                    "system:user:delete",
                    "system:user:reset-password"
            )),
            Map.entry("system:role", List.of(
                    "system:role:create",
                    "system:role:edit",
                    "system:role:delete"
            )),
            Map.entry("basic:supplier", List.of(
                    "basic:supplier:create",
                    "basic:supplier:edit",
                    "basic:supplier:delete"
            )),
            Map.entry("basic:asset-master", List.of(
                    "basic:asset-master:create",
                    "basic:asset-master:edit",
                    "basic:asset-master:delete",
                    "basic:asset-master:status"
            ))
    );

    private PermissionUtil() {
    }

    public static List<String> parseAndExpand(String permissions) {
        if (!StringUtils.hasText(permissions)) {
            return List.of();
        }
        return expandPermissions(Arrays.stream(permissions.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList());
    }

    public static List<String> expandPermissions(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }

        Set<String> expanded = new LinkedHashSet<>();
        List<String> pending = new ArrayList<>(permissions);
        while (!pending.isEmpty()) {
            String permission = pending.remove(0);
            if (!StringUtils.hasText(permission) || !expanded.add(permission)) {
                continue;
            }
            pending.addAll(IMPLIED_PERMISSIONS.getOrDefault(permission, List.of()));
        }
        return List.copyOf(expanded);
    }
}
