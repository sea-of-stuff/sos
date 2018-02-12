package uk.ac.standrews.cs.sos.git_to_sos.impl;

import uk.ac.standrews.cs.sos.git_to_sos.interfaces.Entity;
import uk.ac.standrews.cs.sos.git_to_sos.interfaces.Tree;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TreeImpl extends EntityImpl implements Tree {

    private HashMap<String, Entity> contents;

    public TreeImpl(String id) {
        super(id);

        contents = new LinkedHashMap<>();
    }

    @Override
    public HashMap<String, Entity> getContents() {
        return contents;
    }

    @Override
    public void addContent(String name, Entity entity) {

        contents.put(name, entity);
    }
}
