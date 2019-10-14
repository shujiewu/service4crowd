package cn.edu.buaa.act.management.common;

/**
 * @author wsj
 * 部署的状态
 */
public enum DeploymentState {
    deploying,
    deployed,
    undeployed,
    partial,
    failed,
    error,
    unknown;
}