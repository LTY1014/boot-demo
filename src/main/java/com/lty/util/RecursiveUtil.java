package com.lty.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 递归查询工具类
 */
public class RecursiveUtil {

    /**
     * 递归查询所有子节点ID
     *
     * @param rootId            根节点ID
     * @param allNodes          所有节点列表
     * @param idExtractor       节点ID提取函数 e -> e.getId()
     * @param parentIdExtractor 父节点ID提取函数 e -> e.getParentId()
     * @param <T>               节点类型
     * @param <ID>              ID类型
     * @return 所有子节点ID集合
     * @example
     */
    public static <T, ID> List<ID> getAllSubIds(ID rootId, List<T> allNodes,
                                                Function<T, ID> idExtractor,
                                                Function<T, ID> parentIdExtractor) {
        Set<ID> subSet = new HashSet<>();
        findSubordinatesRecursively(rootId, allNodes, subSet, idExtractor, parentIdExtractor);
        return new ArrayList<>(subSet);
    }

    /**
     * 递归查找所有子节点
     *
     * @param currentId         当前节点ID
     * @param allNodes          所有节点列表
     * @param resultSet         结果集合
     * @param idExtractor       节点ID提取函数
     * @param parentIdExtractor 父节点ID提取函数
     * @param <T>               节点类型
     * @param <ID>              ID类型
     */
    private static <T, ID> void findSubordinatesRecursively(ID currentId, List<T> allNodes,
                                                            Set<ID> resultSet,
                                                            Function<T, ID> idExtractor,
                                                            Function<T, ID> parentIdExtractor) {
        for (T node : allNodes) {
            ID parentId = parentIdExtractor.apply(node);
            // 检查节点的父ID是否等于当前ID
            if (Objects.equals(parentId, currentId)) {
                ID nodeId = idExtractor.apply(node);
                // 如果节点ID不在结果集中，则添加并递归查找其子节点
                if (resultSet.add(nodeId)) {
                    findSubordinatesRecursively(nodeId, allNodes, resultSet, idExtractor, parentIdExtractor);
                }
            }
        }
    }

    /**
     * 递归查询满足条件的所有子节点
     *
     * @param rootId            根节点ID
     * @param allNodes          所有节点列表
     * @param idExtractor       节点ID提取函数
     * @param parentIdExtractor 父节点ID提取函数
     * @param filter            过滤条件
     * @param <T>               节点类型
     * @param <ID>              ID类型
     * @return 满足条件的子节点列表
     */
    public static <T, ID> List<T> getFilteredSubIds(ID rootId, List<T> allNodes,
                                                    Function<T, ID> idExtractor,
                                                    Function<T, ID> parentIdExtractor,
                                                    Predicate<T> filter) {
        List<T> resultList = new ArrayList<>();
        findFilteredSubordinatesRecursively(rootId, allNodes, resultList, idExtractor, parentIdExtractor, filter);
        return resultList;
    }

    /**
     * 递归查找满足条件的所有子节点
     *
     * @param currentId         当前节点ID
     * @param allNodes          所有节点列表
     * @param resultList        结果列表
     * @param idExtractor       节点ID提取函数
     * @param parentIdExtractor 父节点ID提取函数
     * @param filter            过滤条件
     * @param <T>               节点类型
     * @param <ID>              ID类型
     */
    private static <T, ID> void findFilteredSubordinatesRecursively(ID currentId, List<T> allNodes,
                                                                    List<T> resultList,
                                                                    Function<T, ID> idExtractor,
                                                                    Function<T, ID> parentIdExtractor,
                                                                    Predicate<T> filter) {
        for (T node : allNodes) {
            ID parentId = parentIdExtractor.apply(node);
            // 检查节点的父ID是否等于当前ID
            if (Objects.equals(parentId, currentId)) {
                // 如果节点满足过滤条件，则添加到结果列表
                if (filter.test(node)) {
                    resultList.add(node);
                }
                // 无论节点是否满足条件，都递归查找其子节点
                ID nodeId = idExtractor.apply(node);
                findFilteredSubordinatesRecursively(nodeId, allNodes, resultList, idExtractor, parentIdExtractor, filter);
            }
        }
    }
}
