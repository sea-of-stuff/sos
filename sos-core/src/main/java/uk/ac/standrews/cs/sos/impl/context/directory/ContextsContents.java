package uk.ac.standrews.cs.sos.impl.context.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.interfaces.IFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The ContextsContents holds all the information regarding contexts and their contents.
 * The contexts themselves are stored via the DDS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextsContents implements Serializable  {

    // Assign a context to a scope
    // Context GUID --> Scope GUID
    private transient HashMap<IGUID, IGUID> contextsToScopes;

    // Maps the context to the versions belonging to it
    private transient HashMap<IGUID, HashMap<IGUID, ContextContent>> mappings;
    
    public ContextsContents() {
        contextsToScopes = new HashMap<>();
        mappings = new HashMap<>();
    }

    public void addContextScope(IGUID context, IGUID scope) {
        contextsToScopes.put(context, scope);
    }

    public void addMapping(IGUID context, IGUID version, ContextContent content) {

        if (!mappings.containsKey(context)) {
            mappings.put(context, new HashMap<>());
        }

        mappings.get(context).put(version, content);
    }

    /**
     * Get the known values for the content at the given context
     *
     * @param context
     * @param content
     * @return
     */
    public ContextContent get(IGUID context, IGUID content) {

        return mappings.get(context).get(content);
    }

    public boolean has(IGUID context, IGUID content) {

        return mappings.containsKey(content) && mappings.get(context).containsKey(content);
    }

    public Iterator<IGUID> getContents(IGUID context) {
        HashMap<IGUID, ContextContent> contents = mappings.get(context);
        if (contents == null) {
            return Collections.emptyIterator();
        } else {
            return contents.keySet().iterator();
        }
    }

    public HashMap<IGUID, ContextContent> getContentsRows(IGUID context) {
        HashMap<IGUID, ContextContent> contents = mappings.get(context);
        if (contents == null) {
            return new HashMap<>();
        } else {
            return contents;
        }
    }

    ///////////////////
    // Serialization //
    ///////////////////

    public void persist(IFile file) throws IOException {
        // TODO
    }

    public static ContextsContents load(IFile file) throws IOException, ClassNotFoundException {

        // TODO

        return null;
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // TODO
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // TODO
    }
}
