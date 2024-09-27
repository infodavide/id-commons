package org.infodavid.commons.test.condition;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.Preconditions;

public class EnabledOnHostCondition implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled("@EnabledOnHost is not present");

    private static final ConditionEvaluationResult ENABLED_ON_CURRENT_HOST = //
            enabled("Enabled on host: " + SystemUtils.getHostName());

    private static final ConditionEvaluationResult DISABLED_ON_CURRENT_HOST = //
            disabled("Disabled on host: " + SystemUtils.getHostName());

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        final Optional<EnabledOnHost> optional = findAnnotation(context.getElement(), EnabledOnHost.class);

        if (optional.isPresent()) {
            Preconditions.condition(!optional.get().value().isEmpty(), "You must declare a valid expression in @EnabledOnHost");
            final Pattern pattern = Pattern.compile(optional.get().value());

            return pattern.matcher(SystemUtils.getHostName()).matches() ? ENABLED_ON_CURRENT_HOST : DISABLED_ON_CURRENT_HOST;
        }

        return ENABLED_BY_DEFAULT;
    }
}
