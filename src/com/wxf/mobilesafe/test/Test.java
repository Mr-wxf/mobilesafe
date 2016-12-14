package com.wxf.mobilesafe.test;

import java.util.List;
import java.util.Random;

import com.wxf.mobilesafe.db.dao.BlcakNumberDAO;
import com.wxf.mobilesafe.db.dao.CommonNumberDAO;
import com.wxf.mobilesafe.db.dao.CommonNumberDAO.Group;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
     
      public void insert(){
    	  BlcakNumberDAO blcakNumberDAO=BlcakNumberDAO.getInstance(getContext());
    	  for(int i=0;i<100;i++){ 
    	  blcakNumberDAO.insert("110"+i, 1+new Random().nextInt(3)+"" );
    	  }
      }
      public void delete(){
    	  BlcakNumberDAO blcakNumberDAO=BlcakNumberDAO.getInstance(getContext());

    	  blcakNumberDAO.delete("110");
      }
      public void update(){
    	  BlcakNumberDAO blcakNumberDAO=BlcakNumberDAO.getInstance(getContext());
    	  blcakNumberDAO.update("110", "2");
      }
     public void text(){
    	 CommonNumberDAO commonNumberDAO = new CommonNumberDAO();
    	 List<Group> group = commonNumberDAO.getGroup();
    	 for (Group group2 : group) {
			System.out.println(group2.idx);
			System.out.println(group2.name);
		}
     }
}
