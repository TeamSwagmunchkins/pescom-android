package com.example.anjana.pescom.contacts;

/**
 * Created by Anjana on 09-04-2016.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<phoneName> ITEMS = new ArrayList<phoneName>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, phoneName> ITEM_MAP = new HashMap<String, phoneName>();

    //private static final int COUNT = 25;

    static {
        // Add some sample items.

    }

    public DummyContent(ArrayList<phoneName> clist)
    {
        for(phoneName p:clist)
        {

            if(p.number!=null && p.number.length()>=10) {
                    addItem(p);
            }
        }
        Collections.sort(ITEMS, phoneName.NameComparator);
    }

    private static void addItem(phoneName item) {
        int i=0;
        for(phoneName p:ITEMS)
        {
            i=0;
            //clist.add(new phoneName(name, phoneNumber));

            if (p.getName().equals(item.getName())&&p.getNumber().equals(item.getNumber())) {
                i=1;break;
            }


        }
        if(i==0){
            ITEMS.add(item);
            ITEM_MAP.put(item.name, item);
        }
    }



    /**
     * A dummy item representing a piece of content.
     */
    public static class phoneName implements Comparator<phoneName>
    {
        String name;
        String number;
        //ImageView photo;

        public phoneName(String name,String number)
        {
            this.name=name;
            this.number=number;
            // this.photo=photo;
        }
        public String getName()
        {
            return name;
        }
        public String getNumber()
        {
            if (number.length() < 10) {
                return "";
            }
            return number.substring(number.length()-10);
        }
        //public ImageView getPhoto(){return photo;}


        public static Comparator<phoneName> NameComparator = new Comparator<phoneName>() {
            public int compare(phoneName lhs, phoneName rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        };

        @Override
        public int compare(phoneName lhs, phoneName rhs) {

            return lhs.name.compareTo(rhs.name);
        }
    }

}
