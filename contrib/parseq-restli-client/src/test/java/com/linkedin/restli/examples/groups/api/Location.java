
package com.linkedin.restli.examples.groups.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.SetMode;
import com.linkedin.data.template.StringArray;


/**
 * A Location record.
 *
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/Location.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Location
    extends RecordTemplate
{

    private final static Location.Fields _fields = new Location.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"Location\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A Location record. TODO HIGH This should be in common.linkedin\",\"fields\":[{\"name\":\"countryCode\",\"type\":\"string\"},{\"name\":\"postalCode\",\"type\":\"string\"},{\"name\":\"geoPostalCode\",\"type\":\"string\"},{\"name\":\"regionCode\",\"type\":\"int\"},{\"name\":\"latitude\",\"type\":\"float\"},{\"name\":\"longitude\",\"type\":\"float\"},{\"name\":\"geoPlaceCodes\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"gmtOffset\",\"type\":\"float\"},{\"name\":\"usesDaylightSavings\",\"type\":\"boolean\"}]}"));
    private final static RecordDataSchema.Field FIELD_CountryCode = SCHEMA.getField("countryCode");
    private final static RecordDataSchema.Field FIELD_PostalCode = SCHEMA.getField("postalCode");
    private final static RecordDataSchema.Field FIELD_GeoPostalCode = SCHEMA.getField("geoPostalCode");
    private final static RecordDataSchema.Field FIELD_RegionCode = SCHEMA.getField("regionCode");
    private final static RecordDataSchema.Field FIELD_Latitude = SCHEMA.getField("latitude");
    private final static RecordDataSchema.Field FIELD_Longitude = SCHEMA.getField("longitude");
    private final static RecordDataSchema.Field FIELD_GeoPlaceCodes = SCHEMA.getField("geoPlaceCodes");
    private final static RecordDataSchema.Field FIELD_GmtOffset = SCHEMA.getField("gmtOffset");
    private final static RecordDataSchema.Field FIELD_UsesDaylightSavings = SCHEMA.getField("usesDaylightSavings");

    public Location() {
        super(new DataMap(), SCHEMA);
    }

    public Location(DataMap data) {
        super(data, SCHEMA);
    }

    public static Location.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for countryCode
     *
     * @see Location.Fields#countryCode
     */
    public boolean hasCountryCode() {
        return contains(FIELD_CountryCode);
    }

    /**
     * Remover for countryCode
     *
     * @see Location.Fields#countryCode
     */
    public void removeCountryCode() {
        remove(FIELD_CountryCode);
    }

    /**
     * Getter for countryCode
     *
     * @see Location.Fields#countryCode
     */
    public String getCountryCode(GetMode mode) {
        return obtainDirect(FIELD_CountryCode, String.class, mode);
    }

    /**
     * Getter for countryCode
     *
     * @see Location.Fields#countryCode
     */
    public String getCountryCode() {
        return obtainDirect(FIELD_CountryCode, String.class, GetMode.STRICT);
    }

    /**
     * Setter for countryCode
     *
     * @see Location.Fields#countryCode
     */
    public Location setCountryCode(String value, SetMode mode) {
        putDirect(FIELD_CountryCode, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for countryCode
     *
     * @see Location.Fields#countryCode
     */
    public Location setCountryCode(String value) {
        putDirect(FIELD_CountryCode, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for postalCode
     *
     * @see Location.Fields#postalCode
     */
    public boolean hasPostalCode() {
        return contains(FIELD_PostalCode);
    }

    /**
     * Remover for postalCode
     *
     * @see Location.Fields#postalCode
     */
    public void removePostalCode() {
        remove(FIELD_PostalCode);
    }

    /**
     * Getter for postalCode
     *
     * @see Location.Fields#postalCode
     */
    public String getPostalCode(GetMode mode) {
        return obtainDirect(FIELD_PostalCode, String.class, mode);
    }

    /**
     * Getter for postalCode
     *
     * @see Location.Fields#postalCode
     */
    public String getPostalCode() {
        return obtainDirect(FIELD_PostalCode, String.class, GetMode.STRICT);
    }

    /**
     * Setter for postalCode
     *
     * @see Location.Fields#postalCode
     */
    public Location setPostalCode(String value, SetMode mode) {
        putDirect(FIELD_PostalCode, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for postalCode
     *
     * @see Location.Fields#postalCode
     */
    public Location setPostalCode(String value) {
        putDirect(FIELD_PostalCode, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for geoPostalCode
     *
     * @see Location.Fields#geoPostalCode
     */
    public boolean hasGeoPostalCode() {
        return contains(FIELD_GeoPostalCode);
    }

    /**
     * Remover for geoPostalCode
     *
     * @see Location.Fields#geoPostalCode
     */
    public void removeGeoPostalCode() {
        remove(FIELD_GeoPostalCode);
    }

    /**
     * Getter for geoPostalCode
     *
     * @see Location.Fields#geoPostalCode
     */
    public String getGeoPostalCode(GetMode mode) {
        return obtainDirect(FIELD_GeoPostalCode, String.class, mode);
    }

    /**
     * Getter for geoPostalCode
     *
     * @see Location.Fields#geoPostalCode
     */
    public String getGeoPostalCode() {
        return obtainDirect(FIELD_GeoPostalCode, String.class, GetMode.STRICT);
    }

    /**
     * Setter for geoPostalCode
     *
     * @see Location.Fields#geoPostalCode
     */
    public Location setGeoPostalCode(String value, SetMode mode) {
        putDirect(FIELD_GeoPostalCode, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for geoPostalCode
     *
     * @see Location.Fields#geoPostalCode
     */
    public Location setGeoPostalCode(String value) {
        putDirect(FIELD_GeoPostalCode, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public boolean hasRegionCode() {
        return contains(FIELD_RegionCode);
    }

    /**
     * Remover for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public void removeRegionCode() {
        remove(FIELD_RegionCode);
    }

    /**
     * Getter for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public Integer getRegionCode(GetMode mode) {
        return obtainDirect(FIELD_RegionCode, Integer.class, mode);
    }

    /**
     * Getter for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public Integer getRegionCode() {
        return obtainDirect(FIELD_RegionCode, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public Location setRegionCode(Integer value, SetMode mode) {
        putDirect(FIELD_RegionCode, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public Location setRegionCode(Integer value) {
        putDirect(FIELD_RegionCode, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for regionCode
     *
     * @see Location.Fields#regionCode
     */
    public Location setRegionCode(int value) {
        putDirect(FIELD_RegionCode, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for latitude
     *
     * @see Location.Fields#latitude
     */
    public boolean hasLatitude() {
        return contains(FIELD_Latitude);
    }

    /**
     * Remover for latitude
     *
     * @see Location.Fields#latitude
     */
    public void removeLatitude() {
        remove(FIELD_Latitude);
    }

    /**
     * Getter for latitude
     *
     * @see Location.Fields#latitude
     */
    public Float getLatitude(GetMode mode) {
        return obtainDirect(FIELD_Latitude, Float.class, mode);
    }

    /**
     * Getter for latitude
     *
     * @see Location.Fields#latitude
     */
    public Float getLatitude() {
        return obtainDirect(FIELD_Latitude, Float.class, GetMode.STRICT);
    }

    /**
     * Setter for latitude
     *
     * @see Location.Fields#latitude
     */
    public Location setLatitude(Float value, SetMode mode) {
        putDirect(FIELD_Latitude, Float.class, Float.class, value, mode);
        return this;
    }

    /**
     * Setter for latitude
     *
     * @see Location.Fields#latitude
     */
    public Location setLatitude(Float value) {
        putDirect(FIELD_Latitude, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for latitude
     *
     * @see Location.Fields#latitude
     */
    public Location setLatitude(float value) {
        putDirect(FIELD_Latitude, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for longitude
     *
     * @see Location.Fields#longitude
     */
    public boolean hasLongitude() {
        return contains(FIELD_Longitude);
    }

    /**
     * Remover for longitude
     *
     * @see Location.Fields#longitude
     */
    public void removeLongitude() {
        remove(FIELD_Longitude);
    }

    /**
     * Getter for longitude
     *
     * @see Location.Fields#longitude
     */
    public Float getLongitude(GetMode mode) {
        return obtainDirect(FIELD_Longitude, Float.class, mode);
    }

    /**
     * Getter for longitude
     *
     * @see Location.Fields#longitude
     */
    public Float getLongitude() {
        return obtainDirect(FIELD_Longitude, Float.class, GetMode.STRICT);
    }

    /**
     * Setter for longitude
     *
     * @see Location.Fields#longitude
     */
    public Location setLongitude(Float value, SetMode mode) {
        putDirect(FIELD_Longitude, Float.class, Float.class, value, mode);
        return this;
    }

    /**
     * Setter for longitude
     *
     * @see Location.Fields#longitude
     */
    public Location setLongitude(Float value) {
        putDirect(FIELD_Longitude, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for longitude
     *
     * @see Location.Fields#longitude
     */
    public Location setLongitude(float value) {
        putDirect(FIELD_Longitude, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for geoPlaceCodes
     *
     * @see Location.Fields#geoPlaceCodes
     */
    public boolean hasGeoPlaceCodes() {
        return contains(FIELD_GeoPlaceCodes);
    }

    /**
     * Remover for geoPlaceCodes
     *
     * @see Location.Fields#geoPlaceCodes
     */
    public void removeGeoPlaceCodes() {
        remove(FIELD_GeoPlaceCodes);
    }

    /**
     * Getter for geoPlaceCodes
     *
     * @see Location.Fields#geoPlaceCodes
     */
    public StringArray getGeoPlaceCodes(GetMode mode) {
        return obtainWrapped(FIELD_GeoPlaceCodes, StringArray.class, mode);
    }

    /**
     * Getter for geoPlaceCodes
     *
     * @see Location.Fields#geoPlaceCodes
     */
    public StringArray getGeoPlaceCodes() {
        return obtainWrapped(FIELD_GeoPlaceCodes, StringArray.class, GetMode.STRICT);
    }

    /**
     * Setter for geoPlaceCodes
     *
     * @see Location.Fields#geoPlaceCodes
     */
    public Location setGeoPlaceCodes(StringArray value, SetMode mode) {
        putWrapped(FIELD_GeoPlaceCodes, StringArray.class, value, mode);
        return this;
    }

    /**
     * Setter for geoPlaceCodes
     *
     * @see Location.Fields#geoPlaceCodes
     */
    public Location setGeoPlaceCodes(StringArray value) {
        putWrapped(FIELD_GeoPlaceCodes, StringArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public boolean hasGmtOffset() {
        return contains(FIELD_GmtOffset);
    }

    /**
     * Remover for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public void removeGmtOffset() {
        remove(FIELD_GmtOffset);
    }

    /**
     * Getter for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public Float getGmtOffset(GetMode mode) {
        return obtainDirect(FIELD_GmtOffset, Float.class, mode);
    }

    /**
     * Getter for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public Float getGmtOffset() {
        return obtainDirect(FIELD_GmtOffset, Float.class, GetMode.STRICT);
    }

    /**
     * Setter for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public Location setGmtOffset(Float value, SetMode mode) {
        putDirect(FIELD_GmtOffset, Float.class, Float.class, value, mode);
        return this;
    }

    /**
     * Setter for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public Location setGmtOffset(Float value) {
        putDirect(FIELD_GmtOffset, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for gmtOffset
     *
     * @see Location.Fields#gmtOffset
     */
    public Location setGmtOffset(float value) {
        putDirect(FIELD_GmtOffset, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public boolean hasUsesDaylightSavings() {
        return contains(FIELD_UsesDaylightSavings);
    }

    /**
     * Remover for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public void removeUsesDaylightSavings() {
        remove(FIELD_UsesDaylightSavings);
    }

    /**
     * Getter for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public Boolean isUsesDaylightSavings(GetMode mode) {
        return obtainDirect(FIELD_UsesDaylightSavings, Boolean.class, mode);
    }

    /**
     * Getter for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public Boolean isUsesDaylightSavings() {
        return obtainDirect(FIELD_UsesDaylightSavings, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public Location setUsesDaylightSavings(Boolean value, SetMode mode) {
        putDirect(FIELD_UsesDaylightSavings, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public Location setUsesDaylightSavings(Boolean value) {
        putDirect(FIELD_UsesDaylightSavings, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for usesDaylightSavings
     *
     * @see Location.Fields#usesDaylightSavings
     */
    public Location setUsesDaylightSavings(boolean value) {
        putDirect(FIELD_UsesDaylightSavings, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public Location clone()
        throws CloneNotSupportedException
    {
        return ((Location) super.clone());
    }

    @Override
    public Location copy()
        throws CloneNotSupportedException
    {
        return ((Location) super.copy());
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

        public PathSpec countryCode() {
            return new PathSpec(getPathComponents(), "countryCode");
        }

        public PathSpec postalCode() {
            return new PathSpec(getPathComponents(), "postalCode");
        }

        public PathSpec geoPostalCode() {
            return new PathSpec(getPathComponents(), "geoPostalCode");
        }

        public PathSpec regionCode() {
            return new PathSpec(getPathComponents(), "regionCode");
        }

        public PathSpec latitude() {
            return new PathSpec(getPathComponents(), "latitude");
        }

        public PathSpec longitude() {
            return new PathSpec(getPathComponents(), "longitude");
        }

        public PathSpec geoPlaceCodes() {
            return new PathSpec(getPathComponents(), "geoPlaceCodes");
        }

        public PathSpec gmtOffset() {
            return new PathSpec(getPathComponents(), "gmtOffset");
        }

        public PathSpec usesDaylightSavings() {
            return new PathSpec(getPathComponents(), "usesDaylightSavings");
        }

    }

}
