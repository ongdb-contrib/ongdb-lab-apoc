package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO
 * @date 2021/4/14 13:13
 */
public class LoopListResult {

    public List<List<Long>> loopResultList;

    public LoopListResult(List<LoopResult> loopResultList) {
        if (Objects.nonNull(loopResultList) && !loopResultList.isEmpty()) {
            this.loopResultList = loopResultList.parallelStream()
                    .map(LoopResult::getNodeSeqIdList)
                    .collect(Collectors.toList());
        }
    }
}
