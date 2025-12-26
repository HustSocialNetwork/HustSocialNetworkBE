package vn.hust.social.backend.service.target;

import org.springframework.stereotype.Component;
import vn.hust.social.backend.common.response.ResponseCode;
import vn.hust.social.backend.entity.enums.like.TargetType;
import vn.hust.social.backend.exception.ApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TargetStrategyResolver {

    private final Map<TargetType, TargetStrategy> strategies;

    public TargetStrategyResolver(List<TargetStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        TargetStrategy::getTargetType,
                        Function.identity()
                ));
    }

    public TargetStrategy resolve(TargetType type) {
        return Optional.ofNullable(strategies.get(type))
                .orElseThrow(() -> new ApiException(ResponseCode.INVALID_TARGET));
    }
}
