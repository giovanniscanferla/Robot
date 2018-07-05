/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.gestore;

import java.util.Comparator;

/**
 *
 * @author Giovanni
 */
public class ComparatorXML implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        if(o1.toString().charAt(0) == '*' && o2.toString().charAt(0) != '*'){
            return 1;
        } else if(o2.toString().charAt(0) == '*' && o1.toString().charAt(0) != '*'){
            return -1;
        } else if(o1.toString().charAt(0) == '*' && o2.toString().charAt(0) == '*'){
            return o2.toString().length() - o1.toString().length();
        } 
        
        return o1.toString().compareTo(o2.toString());
    }
    
}
