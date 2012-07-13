

package com.prc.tt.utils.fix;


import quickfix.Field;

public class CustomField<T> extends Field<T> {
    public CustomField(int i,
                       T inObject) {
        super(i,
              inObject);
    }
    @Override
    public String toString() {
        return new StringBuffer().append(getTag()).toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + getTag();
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !super.equals(obj) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        final CustomField<?> other = (CustomField<?>)obj;
        if ( getTag() != other.getTag() )
            return false;
        return true;
    }
}

