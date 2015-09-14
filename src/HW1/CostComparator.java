package HW1;

import java.util.Comparator;

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
       return 0;
   }
}