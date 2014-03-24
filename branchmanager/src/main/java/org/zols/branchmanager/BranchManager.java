/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zols.branchmanager;

import org.zols.branchmanager.domain.Branch;
import org.zols.datastore.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author monendra_s
 */
@Service
public class BranchManager {
    @Autowired
    private DataStore dataStore;

    public Branch add(Branch branch) {
        return dataStore.create(branch, Branch.class);
    }

    public void update(Branch branch) {
        dataStore.update(branch, Branch.class);
    }

    public void delete(String branchName) {
        dataStore.delete(branchName, Branch.class);
    }

    public Branch get(String branchName) {
        return dataStore.read(branchName, Branch.class);
    }

    public Page<Branch> list(Pageable page) {
        return dataStore.list(page, Branch.class);
    }
   

    
    
}
