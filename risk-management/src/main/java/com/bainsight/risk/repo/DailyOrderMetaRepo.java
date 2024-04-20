package com.bainsight.risk.repo;

import com.bainsight.risk.Model.Entity.DailyOrderMeta;
import org.springframework.data.repository.CrudRepository;

public interface DailyOrderMetaRepo extends CrudRepository<DailyOrderMeta, String> {
}
