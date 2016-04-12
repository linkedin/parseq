
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.SetMode;


/**
 * metadata for greetings search results
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/SearchMetadata.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class SearchMetadata
    extends RecordTemplate
{

    private final static SearchMetadata.Fields _fields = new SearchMetadata.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"SearchMetadata\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"doc\":\"metadata for greetings search results\",\"fields\":[{\"name\":\"facets\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"ToneFacet\",\"doc\":\"metadata for greetings search results\",\"fields\":[{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]}},{\"name\":\"count\",\"type\":\"int\"}]}}}]}"));
    private final static RecordDataSchema.Field FIELD_Facets = SCHEMA.getField("facets");

    public SearchMetadata() {
        super(new DataMap(), SCHEMA);
    }

    public SearchMetadata(DataMap data) {
        super(data, SCHEMA);
    }

    public static SearchMetadata.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for facets
     * 
     * @see SearchMetadata.Fields#facets
     */
    public boolean hasFacets() {
        return contains(FIELD_Facets);
    }

    /**
     * Remover for facets
     * 
     * @see SearchMetadata.Fields#facets
     */
    public void removeFacets() {
        remove(FIELD_Facets);
    }

    /**
     * Getter for facets
     * 
     * @see SearchMetadata.Fields#facets
     */
    public ToneFacetArray getFacets(GetMode mode) {
        return obtainWrapped(FIELD_Facets, ToneFacetArray.class, mode);
    }

    /**
     * Getter for facets
     * 
     * @see SearchMetadata.Fields#facets
     */
    public ToneFacetArray getFacets() {
        return obtainWrapped(FIELD_Facets, ToneFacetArray.class, GetMode.STRICT);
    }

    /**
     * Setter for facets
     * 
     * @see SearchMetadata.Fields#facets
     */
    public SearchMetadata setFacets(ToneFacetArray value, SetMode mode) {
        putWrapped(FIELD_Facets, ToneFacetArray.class, value, mode);
        return this;
    }

    /**
     * Setter for facets
     * 
     * @see SearchMetadata.Fields#facets
     */
    public SearchMetadata setFacets(ToneFacetArray value) {
        putWrapped(FIELD_Facets, ToneFacetArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public SearchMetadata clone()
        throws CloneNotSupportedException
    {
        return ((SearchMetadata) super.clone());
    }

    @Override
    public SearchMetadata copy()
        throws CloneNotSupportedException
    {
        return ((SearchMetadata) super.copy());
    }

    public static class Fields
        extends PathSpec
    {


        public Fields(List<String> path, String name) {
            super(path, name);
        }

        public Fields() {
            super();
        }

        public com.linkedin.restli.examples.greetings.api.ToneFacetArray.Fields facets() {
            return new com.linkedin.restli.examples.greetings.api.ToneFacetArray.Fields(getPathComponents(), "facets");
        }

    }

}
