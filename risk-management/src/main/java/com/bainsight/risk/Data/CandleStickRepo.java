package com.bainsight.risk.Data;


import com.bainsight.risk.Model.Entity.CandleStick;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CandleStickRepo extends CrudRepository<CandleStick, String> { }
