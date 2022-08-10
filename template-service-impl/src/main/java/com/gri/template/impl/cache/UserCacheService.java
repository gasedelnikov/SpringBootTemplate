package com.gri.template.impl.cache;

import com.gri.template.impl.dao.UserRepository;

public interface UserCacheService extends UserRepository {

    void refresh(String username);

}
