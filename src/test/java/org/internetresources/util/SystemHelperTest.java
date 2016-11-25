package org.internetresources.util;

import static org.fest.assertions.api.Assertions.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.internetresources.util.SystemHelper.MemorySnapshot;
import org.junit.Test;

public class SystemHelperTest {
    private static Log LOG = LogFactory.getLog(SystemHelperTest.class.getName());

	@Test
	public void shoult_print_memory_usage() {
		// GIVEN
		// WHEN
		String memorySnapshot = SystemHelper.getMemorySnapshotString();
		
		//THEN
		LOG.info(memorySnapshot);
		assertThat(memorySnapshot).isNotNull();
	}
	@Test
	public void shoult_get_memory_details() {
		// GIVEN
		// WHEN
		MemorySnapshot memorySnapshot = SystemHelper.getMemorySnapshot();
		
		//THEN
		LOG.info(String.format("%d %% of total used", memorySnapshot.percentUsedTotal));
		LOG.info(String.format("warnMsg:%s", memorySnapshot.getWarnMessage()));
		assertThat(memorySnapshot.percentUsedTotal).isNotNull();
	}

}
