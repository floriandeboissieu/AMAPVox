/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.lidar.format.als;

import java.math.BigInteger;






/**
 *
 * @author Julien
 */
public class LasHeader {

    private String fileSignature;
    private long reserved;
    private long projectIdGuidData1;
    private int projectIdGuidData2;
    private int projectIdGuidData3;
    private double projectIdGuidData4;
    private int versionMajor;
    private int versionMinor;
    private String systemIdentifier;
    private String generatingSoftware;
    private short fileCreationDayOfYear;
    private short fileCreationYear;
    private short headerSize;
    private long offsetToPointData;
    private long numberOfVariableLengthRecords;
    private short pointDataFormatID;
    private short pointDataRecordLength;
    private long numberOfPointrecords;
    private long[] numberOfPointsByReturn;
    private double xScaleFactor;
    private double yScaleFactor;
    private double zScaleFactor;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private double maxX;
    private double minX;
    private double maxY;
    private double minY;
    private double maxZ;
    private double minZ;

    public long getReserved() {
        return reserved;
    }

    public void setReserved(long reserved) {
        this.reserved = reserved;
    }

    
    /**
     * 
     * @param fileSignature 
     */
    public void setFileSignature(char[] fileSignature) {

        this.fileSignature = String.valueOf(fileSignature);
    }    
    
    /**
     * 
     * @param projectIdGuidData1
     */
    public void setProjectIdGuidData1(long projectIdGuidData1) {
        this.projectIdGuidData1 = projectIdGuidData1;
    }

    /**
     * 
     * @param projectIdGuidData2
     */
    public void setProjectIdGuidData2(int projectIdGuidData2) {
        this.projectIdGuidData2 = projectIdGuidData2;
    }

    /**
     * 
     * @param projectIdGuidData3
     */
    public void setProjectIdGuidData3(int projectIdGuidData3) {
        this.projectIdGuidData3 = projectIdGuidData3;
    }

    /**
     * 
     * @param projectIdGuidData4
     */
    public void setProjectIdGuidData4(double projectIdGuidData4) {
        this.projectIdGuidData4 = projectIdGuidData4;
    }

    /**
     * 
     * @param versionMajor
     */
    public void setVersionMajor(byte versionMajor) {
        this.versionMajor = (int) versionMajor;
    }

    /**
     * 
     * @param versionMinor
     */
    public void setVersionMinor(byte versionMinor) {
        this.versionMinor = (int) versionMinor;
    }
    
    /**
     * 
     * @return  The version 1.0 specification assumes that LAS files are exclusively generated 
    as a result of collection by a hardware sensor. Version 1.1 recognizes that files often result from 
    extraction, merging or modifying existing data files. <br/>
    
    <table border=1>
    <tr><th>Generating agent</th> <th>System ID</th></tr>
    
    <tr><td>Hardware system</td>                          <td>String identifying hardware (e.g. “ALTM 1210” or “ALS50”)</td></tr>
    <tr><td>Merge of one or more files</td>               <td>“MERGE”</td></tr>
    <tr><td>Modification of a single file</td>            <td>“MODIFICATION” </td></tr>
    <tr><td>Extraction from one or more files</td>        <td>“EXTRACTION” </td></tr>
    <tr><td>Reprojection, rescaling, warping, etc.</td>   <td>“TRANSFORMATION”</td></tr>
    </table>
    Some other operation “OTHER” or a string up to 32 characters 
    identifying the operation 
     */
    
    public String getSystemIdentifier() {
        return systemIdentifier;
    }
    /**
     * 
     * @param generatingSoftware
     */
    public void setGeneratingSoftware(char[] generatingSoftware) {
        this.generatingSoftware = String.valueOf(generatingSoftware);
    }
    /**
     * 
     * @param fileCreationDayOfYear
     */
    protected void setFileCreationDayOfYear(short fileCreationDayOfYear) {
        this.fileCreationDayOfYear = fileCreationDayOfYear;
    }

    /**
     * 
     * @param fileCreationYear
     */
    public void setFileCreationYear(short fileCreationYear) {
        this.fileCreationYear = fileCreationYear;
    }

    /**
     * 
     * @param headerSize
     */
    public void setHeaderSize(short headerSize) {
        this.headerSize = headerSize;
    }


    /**
     * 
     * @param versionMajor
     */
    public void setVersionMajor(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    /**
     * 
     * @param versionMinor
     */
    public void setVersionMinor(int versionMinor) {
        this.versionMinor = versionMinor;
    }

    /**
     * 
     * @param offsetToPointData
     */
    public void setOffsetToPointData(long offsetToPointData) {
        this.offsetToPointData = offsetToPointData;
    }

    /**
     * 
     * @param numberOfVariableLengthRecords
     */
    public void setNumberOfVariableLengthRecords(long numberOfVariableLengthRecords) {
        this.numberOfVariableLengthRecords = numberOfVariableLengthRecords;
    }

    /**
     * 
     * @param pointDataFormatID
     */
    public void setPointDataFormatID(byte pointDataFormatID) {
        this.pointDataFormatID = (short) pointDataFormatID;
    }

    /**
     * 
     * @param pointDataRecordLength
     */
    public void setPointDataRecordLength(short pointDataRecordLength) {
        this.pointDataRecordLength = pointDataRecordLength;
    }

    /**
     * 
     * @param numberOfPointsByReturn
     */
    public void setNumberOfPointsByReturn(long[] numberOfPointsByReturn) {
        this.numberOfPointsByReturn = numberOfPointsByReturn;
    }

    /**
     * 
     * @param xScaleFactor
     */
    public void setxScaleFactor(double xScaleFactor) {
        this.xScaleFactor = xScaleFactor;
    }

    /**
     * 
     * @param yScaleFactor
     */
    public void setyScaleFactor(double yScaleFactor) {
        this.yScaleFactor = yScaleFactor;
    }

    /**
     * 
     * @param zScaleFactor
     */
    public void setzScaleFactor(double zScaleFactor) {
        this.zScaleFactor = zScaleFactor;
    }

    /**
     * 
     * @param xOffset
     */
    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    /**
     * 
     * @param yOffset
     */
    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    /**
     * 
     * @param zOffset
     */
    public void setzOffset(double zOffset) {
        this.zOffset = zOffset;
    }

    /**
     * 
     * @param maxX
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * 
     * @param minX
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * 
     * @param maxY
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     * 
     * @param minY
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * 
     * @param maxZ
     */
    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    /**
     * 
     * @param minZ
     */
    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    /**
     * 
     * @param numberOfPointrecords
     */
    public void setNumberOfPointrecords(long numberOfPointrecords) {
        this.numberOfPointrecords = numberOfPointrecords;
    }

    /**
     * 
     * @return The file signature must contain the four characters “LASF”, and it is required by 
    the LAS specification.
     */
    public String getFileSignature() {
        return fileSignature;
    }

    /**
     * 
     * @return The version number consists of a major and minor field. The major and minor 
    fields combine to form the number that indicates the format number of the current specification 
    itself. <br/>For example, specification number 1.2 (this version) would contain 1 in the major field and 
    2 in the minor field. 
     */
    public int getVersionMajor() {
        return versionMajor;
    }

    /**
     * 
     * @return The version number consists of a major and minor field. The major and minor 
    fields combine to form the number that indicates the format number of the current specification 
    itself. <br/>For example, specification number 1.2 (this version) would contain 1 in the major field and 
    2 in the minor field. 
     */
    public int getVersionMinor() {
        return versionMinor;
    }

    /**
     * 
     * @param systemIdentifier
     */
    public void setSystemIdentifier(char[] systemIdentifier) {
        this.systemIdentifier = String.valueOf(systemIdentifier);
    }
    
    /**
     * 
     * @return This information is ASCII data describing the generating software itself. <br/>
    This field provides a mechanism for specifying which generating software package and version 
    was used during LAS file creation (e.g. “TerraScan V-10.8”, “REALM V-4.2” and etc.). <br/>If the 
    character data is less than 16 characters, the remaining data must be null. 
     */
    public String getGeneratingSoftware() {
        return generatingSoftware;
    }
    
    
    /**
     * 
     * @return  The julian day of the year that the data was collected. This field should be 
    populated by the generating software. 
     */
    public int getFileCreationDayOfYear() {
        return fileCreationDayOfYear;
    }

    /**
     * 
     * @return The year, expressed as a four digit number, in which the file was created. 
     */
    public int getFileCreationYear() {
        return fileCreationYear;
    }

    /**
     * 
     * @return The size, in bytes, of the Public Header Block itself. <br/>In the event that the header is 
    extended by a software application through the addition of data at the end of the header, the 
    Header Size field must be updated with the new header size. <br/>Extension of the Public Header 
    Block is discouraged; the Variable Length Records should be used whenever possible to add 
    custom header data. <br/>In the event a generating software package adds data to the Public Header 
    Block, this data must be placed at the end of the structure and the Header Size must be updated 
    to reflect the new size. 
     */
    public int getHeaderSize() {
        return headerSize;
    }

    /**
     * 
     * @return The actual number of bytes from the beginning of the file to the first field 
    of the first point record data field.<br/> This data offset must be updated if any software adds data 
    from the Public Header Block or adds/removes data to/from the Variable Length Records.
     */
    public long getOffsetToPointData() {
        return offsetToPointData;
    }

    /**
     * 
     * @return This field contains the current number of Variable Length 
    Records.<br/> This number must be updated if the number of Variable Length Records changes at 
    any time. 
     */
    public long getNumberOfVariableLengthRecords() {
        return numberOfVariableLengthRecords;
    }

    /**
     * 
     * @return The point data format ID corresponds to the point data record format 
    type. LAS 1.2 defines types 0, 1, 2 and 3. 
     */
    public int getPointDataFormatID() {
        return pointDataFormatID;
    }

    /**
     * 
     * @return The size, in bytes, of the Point Data Record. 
     */
    public short getPointDataRecordLength() {
        return pointDataRecordLength;
    }

    /**
     * 
     * @return This field contains the total number of point records within the file. 
     */
    public long getNumberOfPointrecords() {
        return numberOfPointrecords;
    }

    /**
     * 
     * @return This field contains an array of the total point records per return.<br/> 
    The first unsigned long value will be the total number of records from the first return, and the 
    second contains the total number for return two, and so forth up to five returns. 
     */
    public long[] getNumberOfPointsByReturn() {
        return numberOfPointsByReturn;
    }

    /**
     * 
     * @return  The scale factor fields contain a double floating point value that is used 
    to scale the corresponding X, Y, and Z long values within the point records. The corresponding 
    X, Y, and Z scale factor must be multiplied by the X, Y, or Z point record value to get the actual 
    X, Y, or Z coordinate.<br/> For example, if the X, Y, and Z coordinates are intended to have two 
    decimal point values, then each scale factor will contain the number 0.01. 
     */
    public double getxScaleFactor() {
        return xScaleFactor;
    }

    /**
     * 
     * @return  The scale factor fields contain a double floating point value that is used 
    to scale the corresponding X, Y, and Z long values within the point records. The corresponding 
    X, Y, and Z scale factor must be multiplied by the X, Y, or Z point record value to get the actual 
    X, Y, or Z coordinate.<br/> For example, if the X, Y, and Z coordinates are intended to have two 
    decimal point values, then each scale factor will contain the number 0.01. 
     */
    public double getyScaleFactor() {
        return yScaleFactor;
    }

    /**
     * 
     * @return  The scale factor fields contain a double floating point value that is used 
    to scale the corresponding X, Y, and Z long values within the point records. The corresponding 
    X, Y, and Z scale factor must be multiplied by the X, Y, or Z point record value to get the actual 
    X, Y, or Z coordinate.<br/> For example, if the X, Y, and Z coordinates are intended to have two 
    decimal point values, then each scale factor will contain the number 0.01. 
     */
    public double getzScaleFactor() {
        return zScaleFactor;
    }

    /**
     * 
     * @return The offset fields should be used to set the overall offset for the point records.<br/> 
    In general these numbers will be zero, but for certain cases the resolution of the point data may 
    not be large enough for a given projection system.<br/> However, it should always be assumed that 
    these numbers are used. So to scale a given X from the point record, take the point record X 
    multiplied by the X scale factor, and then add the X offset.<br/> 
    Xcoordinate = (Xrecord * Xscale) + Xoffset<br/>
    Ycoordinate = (Yrecord * Yscale) + Yoffset<br/>
    Zcoordinate = (Zrecord * Zscale) + Zoffset<br/> 
     */
    public double getxOffset() {
        return xOffset;
    }

    /**
     * 
     * @return The offset fields should be used to set the overall offset for the point records. <br/>
    In general these numbers will be zero, but for certain cases the resolution of the point data may 
    not be large enough for a given projection system. However, it should always be assumed that 
    these numbers are used. So to scale a given X from the point record, take the point record X 
    multiplied by the X scale factor, and then add the X offset. <br/>
    Xcoordinate = (Xrecord * Xscale) + Xoffset
    Ycoordinate = (Yrecord * Yscale) + Yoffset 
    Zcoordinate = (Zrecord * Zscale) + Zoffset 
     */
    public double getyOffset() {
        return yOffset;
    }

    /**
     * 
     * @return The offset fields should be used to set the overall offset for the point records. <br/>
    In general these numbers will be zero, but for certain cases the resolution of the point data may 
    not be large enough for a given projection system. However, it should always be assumed that 
    these numbers are used. So to scale a given X from the point record, take the point record X 
    multiplied by the X scale factor, and then add the X offset. <br/>
    Xcoordinate = (Xrecord * Xscale) + Xoffset
    Ycoordinate = (Yrecord * Yscale) + Yoffset 
    Zcoordinate = (Zrecord * Zscale) + Zoffset 
     */
    public double getzOffset() {
        return zOffset;
    }

    /**
     * 
     * @return  The max and min data fields are the actual unscaled extents of the LAS 
    point file data, specified in the coordinate system of the LAS data. 
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * 
     * @return  The max and min data fields are the actual unscaled extents of the LAS 
    point file data, specified in the coordinate system of the LAS data. 
     */
    public double getMinX() {
        return minX;
    }

    /**
     * 
     * @return  The max and min data fields are the actual unscaled extents of the LAS 
    point file data, specified in the coordinate system of the LAS data. 
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * 
     * @return  The max and min data fields are the actual unscaled extents of the LAS 
    point file data, specified in the coordinate system of the LAS data. 
     */
    public double getMinY() {
        return minY;
    }

    /**
     * 
     * @return  The max and min data fields are the actual unscaled extents of the LAS 
    point file data, specified in the coordinate system of the LAS data. 
     */
    public double getMaxZ() {
        return maxZ;
    }

    /**
     * 
     * @return  The max and min data fields are the actual unscaled extents of the LAS 
    point file data, specified in the coordinate system of the LAS data. 
     */
    public double getMinZ() {
        return minZ;
    }
    
    /**
     * 
     * @return The four fields that comprise a complete Globally Unique Identifier 
    (GUID) are now reserved for use as a Project Identifier (Project ID). The field remains optional. <br/>
    The time of assignment of the Project ID is at the discretion of processing software. The Project 
    ID should be the same for all files that are associated with a unique project.<br/> By assigning a 
    Project ID and using a File Source ID (defined above) every file within a project and every point 
    within a file can be uniquely identified, globally. 
     */
    public long getProjectIdGuidData1() {
        return projectIdGuidData1;
    }

    /**
     * 
     * @return The four fields that comprise a complete Globally Unique Identifier 
    (GUID) are now reserved for use as a Project Identifier (Project ID). The field remains optional. <br/>
    The time of assignment of the Project ID is at the discretion of processing software. The Project 
    ID should be the same for all files that are associated with a unique project. <br/>By assigning a 
    Project ID and using a File Source ID (defined above) every file within a project and every point 
    within a file can be uniquely identified, globally. 
     */
    public int getProjectIdGuidData2() {
        return projectIdGuidData2;
    }
    
    /**
     * 
     * @return The four fields that comprise a complete Globally Unique Identifier 
    (GUID) are now reserved for use as a Project Identifier (Project ID). The field remains optional.<br/> 
    The time of assignment of the Project ID is at the discretion of processing software. The Project 
    ID should be the same for all files that are associated with a unique project. <br/>By assigning a 
    Project ID and using a File Source ID (defined above) every file within a project and every point 
    within a file can be uniquely identified, globally. 
     */
    public int getProjectIdGuidData3() {
        return projectIdGuidData3;
    }

    /**
     * 
     * @return The four fields that comprise a complete Globally Unique Identifier 
    (GUID) are now reserved for use as a Project Identifier (Project ID).The field remains optional. <br/>
    The time of assignment of the Project ID is at the discretion of processing software. The Project 
    ID should be the same for all files that are associated with a unique project.<br/> By assigning a 
    Project ID and using a File Source ID (defined above) every file within a project and every point 
    within a file can be uniquely identified, globally. 
     */
    public double getProjectIdGuidData4() {
        return projectIdGuidData4;
    }
    
    public int getFileSourceId() {
        return 0;
    }
}
class LasHeader11 extends LasHeader {

    private int globalEncoding;
    private int fileSourceId;

    public int getGlobalEncoding() {
        return globalEncoding;
    }

    public void setGlobalEncoding(int globalEncoding) {
        this.globalEncoding = globalEncoding;
    }

    /**
     * 
     * @return This field is a value between 1 and 65,535, inclusive. A value of zero (0) is interpreted to 
    mean that an ID has not been assigned. In this case, processing software is free to assign any valid number.<br/>
    Note that this scheme allows a LIDAR project to contain up to 65,535 unique 
    sources. A source can be considered an original flight line or it can be the result of merge and/or 
    extract operations. 
     */
    public int getFileSourceId() {
        return fileSourceId;
    }

    /**
     * 
     * @return  Day, expressed as an unsigned short, on which this file was created. 
    Day is computed as the Greenwich Mean Time (GMT) day. January 1 is considered day 1. 
     */
    @Override
    public int getFileCreationDayOfYear() {
        return super.getFileCreationDayOfYear();
    }

    /**
     * 
     * @param fileSourceId
     */
    public void setFileSourceId(int fileSourceId) {
        this.fileSourceId = fileSourceId;
    }


}


class LasHeader12 extends LasHeader11 {
    
    private int globalEncoding;

    public int getGlobalEncoding() {
        return globalEncoding;
    }

    public void setGlobalEncoding(int globalEncoding) {
        this.globalEncoding = globalEncoding;
    }
}


class LasHeader13 extends LasHeader12 {
    
    private BigInteger startOfWaveformDataPacketRecord;

    public BigInteger getStartOfWaveformDataPacketRecord() {
        return startOfWaveformDataPacketRecord;
    }

    public void setStartOfWaveformDataPacketRecord(BigInteger startOfWaveformDataPacketRecord) {
        this.startOfWaveformDataPacketRecord = startOfWaveformDataPacketRecord;
    }
    
}

class LasHeader14 extends LasHeader13 {
    
    private BigInteger StartOfFirstExtendedVariableLengthRecord;
    private long NumberOfExtendedVariableLengthRecords;
    private BigInteger extendedNumberOfPointRecords;
    private BigInteger[] extendedNumberOfPointsByReturn;

    public long getNumberOfExtendedVariableLengthRecords() {
        return NumberOfExtendedVariableLengthRecords;
    }

    public void setNumberOfExtendedVariableLengthRecords(long NumberOfExtendedVariableLengthRecords) {
        this.NumberOfExtendedVariableLengthRecords = NumberOfExtendedVariableLengthRecords;
    }

    public BigInteger getStartOfFirstExtendedVariableLengthRecord() {
        return StartOfFirstExtendedVariableLengthRecord;
    }

    public void setStartOfFirstExtendedVariableLengthRecord(BigInteger StartOfFirstExtendedVariableLengthRecord) {
        this.StartOfFirstExtendedVariableLengthRecord = StartOfFirstExtendedVariableLengthRecord;
    }

    public BigInteger getExtendedNumberOfPointRecords() {
        return extendedNumberOfPointRecords;
    }

    public void setExtendedNumberOfPointRecords(BigInteger extendedNumberOfPointRecords) {
        this.extendedNumberOfPointRecords = extendedNumberOfPointRecords;
    }

    public BigInteger[] getExtendedNumberOfPointsByReturn() {
        return extendedNumberOfPointsByReturn;
    }

    public void setExtendedNumberOfPointsByReturn(BigInteger[] extendedNumberOfPointsByReturn) {
        this.extendedNumberOfPointsByReturn = extendedNumberOfPointsByReturn;
    }
    
}
