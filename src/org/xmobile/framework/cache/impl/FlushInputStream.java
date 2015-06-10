package org.xmobile.framework.cache.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FlushInputStream extends FilterInputStream {
	public FlushInputStream(InputStream in) {
		super(in);
	}

	@Override
	public long skip(long byteCount) throws IOException {
		long byteTotalSkip = 0;
		while(byteTotalSkip < byteCount){
			long skip = in.skip(byteCount - byteTotalSkip);
			if(skip == 0){
				int eof = read();
				if(eof < 0){
					break;
				}else{
					skip = 1;
				}
			}
			byteTotalSkip += skip;
		}
		return byteTotalSkip;
	}
}
