package jfl;

import java.util.*;
import org.json.JSONObject;

public class Search {
    private final ArrayList<String> 
        kinks=new ArrayList(),
        genders=new ArrayList(),
        orientations=new ArrayList(),
        languages=new ArrayList(),
        furryprefs=new ArrayList(),
        roles=new ArrayList();

   
    public Search(String... searchKinks) {
        kinks.addAll(Arrays.asList(searchKinks));
    }  
    
    public Search(Integer... searchKinks) {
        for (Integer searchKink : searchKinks) 
            kinks.add(searchKink.toString());
    }  

    public Search(Kink... searchKinks) {
        for (Kink searchKink : searchKinks) 
            kinks.add(searchKink.getIDString()); 
    }  
    
    public ArrayList<String> getKinksIDs() {
        return kinks;
    }
 
    private void insert(String[] array, ArrayList<String> arrayList) {
        for (String item:array) {
            if (!arrayList.contains(item));
            arrayList.add(item);
        }
    }
        
    public ArrayList<String> getGenders() {
        return genders;
    }
    
    public Search addGenders(String... searchGenders) {
        insert(searchGenders,genders);
        return this;
    }

    public ArrayList<String> getOrientations() {
        return orientations;
    }
        
    public Search addOrientations(String... searchOrientations) {
        insert(searchOrientations,orientations);
        return this;
    }
    
    public ArrayList<String> getFurryPrefs() {
        return furryprefs;
    }
    
    public Search addFurryPrefs(String... searchFurryprefs) {
        insert(searchFurryprefs,furryprefs);
        return this;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }
    
    public Search addRoles(String... searchRoles) {
        insert(searchRoles,roles);
        return this;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public Search addLanguages(String... searchLanguages) {
        insert(searchLanguages,languages);
        return this;
    }
    
    public JSONObject getJSON() {
        JSONObject obj=new JSONObject();
        obj.put("kinks",FUtil.arrayListToJSON(kinks));
        
        if (!genders.isEmpty()) obj.put("genders",FUtil.arrayListToJSON(genders));
        if (!orientations.isEmpty()) obj.put("orientations",FUtil.arrayListToJSON(orientations));
        if (!languages.isEmpty()) obj.put("languages",FUtil.arrayListToJSON(languages));
        if (!furryprefs.isEmpty()) obj.put("furryprefs",FUtil.arrayListToJSON(furryprefs));
        if (!roles.isEmpty()) obj.put("roles",FUtil.arrayListToJSON(roles));
        
        return obj;
    }
    
    @Override public String toString() {
        return getJSON().toString();
    }
}
