package org.device.demo;

import com.taliter.fiscal.device.FiscalPacket;

/**
 * Created by Ирина on 13.11.2015.
 */
public interface FiscalPacketListener {
  void invoke(FiscalPacket packet);
}
