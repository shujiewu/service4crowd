package cn.edu.buaa.act.figureeight.service;

import cn.edu.buaa.act.figureeight.model.Unit;

/**
 * UnitService
 *
 * @author wsj
 * @date 2018/10/27
 */
public interface UnitService {
    Unit getUnit(String aJobId, String aUnitId,String apiKey);
    Unit create(Unit aUnit,String apiKey);
    void update(Unit aUnit,String apiKey);
    void delete(String aJobId, String aUnitId,String apiKey);
    void addGold(Unit aUnit, String legend, String value, String reason,String apiKey);
    void removeGold(String aJobId, String aUnitId,String apiKey);
}
