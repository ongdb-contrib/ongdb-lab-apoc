package data.lab.ongdb.result;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.result
 * @Description: TODO(环路检测)
 * @date 2021/4/14 10:54
 */
public class LoopResult {

    /**
     * PATH NODE SEQUENCE:0->7->2->8->3->5->4->6->1->0
     * */
    private final static String PATH_REL_JOINT = "->";

    /*
     * 环路的节点序列
     * */
    private List<Long> nodeSeqIdList;

    public LoopResult(Long[] nodeSeqIds) {
        this.nodeSeqIdList = Arrays.asList(nodeSeqIds);
    }

    public LoopResult(List<Long> nodeSeqIds) {
        this.nodeSeqIdList = nodeSeqIds;
    }

    public LoopResult(String pathStr) {
        this.nodeSeqIdList = Arrays.asList(pathStr.split(PATH_REL_JOINT))
                .parallelStream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public LoopResult(String pathStr, Map<Long, Long> indexNode) {
        this.nodeSeqIdList = Arrays.asList(pathStr.split(PATH_REL_JOINT))
                .parallelStream()
                .map(v-> indexNode.get(Long.parseLong(v)))
                .collect(Collectors.toList());
    }

    public List<Long> getNodeSeqIdList() {
        return nodeSeqIdList;
    }

    public void setNodeSeqIdList(List<Long> nodeSeqIdList) {
        this.nodeSeqIdList = nodeSeqIdList;
    }
}
