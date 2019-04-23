/**
 * Licensed under the Artistic License; you may not use this file
 * except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://displaytag.sourceforge.net/license.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.displaytag.export;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.displaytag.model.TableModel;


/**
 * Export view for comma separated value exporting.
 * @author Fabrizio Giustina
 * @version $Revision: 1081 $ ($Author: fgiust $)
 */
public class CsvView extends BaseExportView
{

    /**
     * @see BaseExportView#setParameters(TableModel, boolean, boolean, boolean)
     */
    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader,
        boolean decorateValues)
    {
        super.setParameters(tableModel, exportFullList, includeHeader, decorateValues);
    }

    /**
     * @see BaseExportView#getRowEnd()
     */
    protected String getRowEnd()
    {
        return "\n"; //$NON-NLS-1$
    }

    /**
     * @see BaseExportView#getCellEnd()
     */
    protected String getCellEnd()
    {
        return ","; //$NON-NLS-1$
    }

    /**
     * @see BaseExportView#getAlwaysAppendCellEnd()
     */
    protected boolean getAlwaysAppendCellEnd()
    {
        return false;
    }

    /**
     * @see BaseExportView#getAlwaysAppendRowEnd()
     */
    protected boolean getAlwaysAppendRowEnd()
    {
        return true;
    }

    /**
     * @see ExportView#getMimeType()
     */
    public String getMimeType()
    {
	 return "text/csv;charset=GB2312"; //$NON-NLS-1$
        //return "text/csv"; //$NON-NLS-1$
    }

    /**
     * Escaping for csv format.
     * <ul>
     * <li>Quotes inside quoted strings are escaped with a /</li>
     * <li>Fields containings newlines or , are surrounded by "</li>
     * </ul>
     * Note this is the standard CVS format and it's not handled well by excel.
     * @see BaseExportView#escapeColumnValue(Object)
     */
    protected String escapeColumnValue(Object value)
    {
        String stringValue = StringUtils.trim(value.toString());
        if (!StringUtils.containsNone(stringValue, new char[]{'\n', ','}))
        {
            return "\"" + //$NON-NLS-1$
                StringUtils.replace(stringValue, "\"", "\\\"") + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

      return ExportUtil.removeHtml(stringValue) ;
    }

}
