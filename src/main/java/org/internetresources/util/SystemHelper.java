package org.internetresources.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemHelper {
    private static Log LOG = LogFactory.getLog(SystemHelper.class.getName());
	private static final String PROPERTIES_PLACEHOLDER_PREFIX = "${";
    private static final String ARCH_DATA_MODEL = System.getProperty("sun.arch.data.model");
    private static String OS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

    static boolean isOs(String osSubstring) {
        return (OS != null && OS.indexOf(osSubstring) >= 0);
    }
    public static boolean isWindows() {
        return isOs("win");
    }

    public static boolean isMac() {
        return isOs("mac");
    }

    public static boolean isUnix() {
        return isOs("nix") 
            || isOs("nux")
            || isOs("aix");
    }

    public static boolean isSolaris() {
        return isOs("sunos");
    }

    public static String getOS() {
        return OS;
    }

    public static boolean is32Bits() {
        return "32".equals(ARCH_DATA_MODEL);
    }

    public static boolean is64Bits() {
        return "64".equals(ARCH_DATA_MODEL);
    }

    public static String getArchDataModel() {
        return ARCH_DATA_MODEL;
    }

    /**
     * http://stackoverflow.com/questions/7348711/recommended-way-to-get-hostname-in-java
     * @return String hostname
     */
    public static String getHostname() {
    	// try InetAddress.LocalHost first;
    	// NOTE -- InetAddress.getLocalHost().getHostName() will not work in certain environments.
    	try {
    	    String result = InetAddress.getLocalHost().getHostName();
    	    if (StringUtils.isNotEmpty(result))
    	        return result;
    	} catch (UnknownHostException e) {
    	    // failed;  try alternate means.
    	}
    	// try environment properties.
    	String host = System.getenv("COMPUTERNAME");
    	if (host != null)
    	    return host;
    	host = System.getenv("HOSTNAME");
    	if (host != null)
    	    return host;
    	// undetermined.
    	return null;
    }
    
    // Src: http://viralpatel.net/blogs/getting-jvm-heap-size-used-memory-total-memory-using-java-runtime/
	public static String getMemorySnapshotString() {
		return getMemorySnapshot().toString();
	}
	public static SystemHelper.MemorySnapshot getMemorySnapshot() {
		return new SystemHelper.MemorySnapshot();
	}

	public static class MemorySnapshot {
		private static final int WARN_PERCENT_TOTAL_OF_MAX = 80;

		private int mb = 1024*1024;

		long totalMemoryByte;
		long freeMemoryByte;
		long usedMemoryByte;
		long maxMemoryByte;
		int percentUsedTotal;
		int percentTotalMax;
		int percentFreeTotal;

		public MemorySnapshot() {
			Runtime runtime = Runtime.getRuntime();
			totalMemoryByte  = runtime.totalMemory();
			freeMemoryByte   = runtime.freeMemory();
			usedMemoryByte   = totalMemoryByte - freeMemoryByte;
			maxMemoryByte = runtime.maxMemory();
			
			percentUsedTotal = Math.round(100*usedMemoryByte/totalMemoryByte);
			percentFreeTotal = Math.round(100*freeMemoryByte/totalMemoryByte);
			percentTotalMax   = Math.round(100*totalMemoryByte/maxMemoryByte);
		}
		public String toString() {
			StringBuilder memSnap = new StringBuilder();
			memSnap.append(usedMemoryByte / mb).append("MB (").append(percentUsedTotal).append("%) used of ")
			       // .append(freeMem / mb).append("(").append(percentFreeTotal).append("%) = ")
				   .append(totalMemoryByte / mb).append(" MB total (").append(percentTotalMax).append("% of max:")
				   .append(maxMemoryByte / mb).append(")");
			return memSnap.toString();
		}
		public Boolean isWarn() {
			boolean rez = percentTotalMax > WARN_PERCENT_TOTAL_OF_MAX;
			return Boolean.valueOf(rez);
		}
		public String getWarnMessage() {
			return String.format("total memory is using %d %% of total memory (> %d)", percentTotalMax, WARN_PERCENT_TOTAL_OF_MAX);
		}
	}

   /**
     * Gets the value for the specified key as int or null if not found.
     *
     * @param key
     *            the key
     * @return the int
     */
    public static Integer getEnvInt(String key) {
        String value = System.getenv(key);
        if (value == null) {
        	return null;
        }
        if (value.startsWith(PROPERTIES_PLACEHOLDER_PREFIX)) {
        	String exMsg = String.format("Invalid value for '%s' : Properties placeholder instead of value", key);
        	LOG.error(exMsg);
        	throw new InvalidParameterException(exMsg);
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
        	String exMsg = String.format("Invalid integer value for '%s'", key);
        	LOG.error(exMsg);
        	throw new InvalidParameterException(exMsg);
        }
    }

	public static int getEnvIntWithConstraint(String fieldKey, int min, int max, int defaultVal) {
		Integer fieldValue = getEnvInt(fieldKey);
		if (fieldValue != null && fieldValue >= min && fieldValue <= max) {
			return fieldValue;
		}
		String warnMsg = String.format("Configuration Field %s(=%s) is invalid: expected range is '%d-%d', use default value instead: %d", 
				fieldKey, fieldValue, min, max, defaultVal);
		LOG.warn(warnMsg);
		return defaultVal;
	}
}
