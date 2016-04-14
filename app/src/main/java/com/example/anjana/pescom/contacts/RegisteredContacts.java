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
public class RegisteredContacts {

    /**
     * An array of sample (dummy) items.
     */
    public final List<Contact> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public final Map<String, Contact> ITEM_MAP = new HashMap<>();

    //private static final int COUNT = 25;

    public RegisteredContacts(ArrayList<Contact> clist)
    {
        for(Contact p:clist)
        {

            if(p.number!=null && p.number.length()>=10) {
                    addItem(p);
            }
        }
        Collections.sort(ITEMS, Contact.NameComparator);
    }

    private void addItem(Contact item) {
        int i=0;
        for(Contact p:ITEMS)
        {
            i=0;
            //clist.add(new Contact(name, phoneNumber));

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
    public static class Contact implements Comparator<Contact>
    {
        private final String name;
        private final String number;

        public Contact(String name, String number)
        {
            this.name=name;
            this.number=number;
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

        public static Comparator<Contact> NameComparator = new Comparator<Contact>() {
            public int compare(Contact lhs, Contact rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        };

        @Override
        public int compare(Contact lhs, Contact rhs) {

            return lhs.name.compareTo(rhs.name);
        }
    }

}
