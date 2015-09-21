package HW1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CostComparator implements Comparator<UCSNode>
{
   @Override
   public int compare(UCSNode x, UCSNode y)
   {
       if (x.getCost() < y.getCost())
       {
           return -1;
       }
       if (x.getCost() > y.getCost())
       {
           return 1;
       }
       if(x.getCost()==y.getCost()){
    	   List<String> tmp = new ArrayList<String>();
    	   tmp.add(x.getState());
    	   tmp.add(y.getState());
    	   Collections.sort(tmp);
    	   if(tmp.get(0).equals(x.getState())){
    		   return -1;
    	   }else{
    		   return 1;
    	   }
       }
       return 0;
   }
}