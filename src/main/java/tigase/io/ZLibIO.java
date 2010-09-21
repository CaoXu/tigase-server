/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2008 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */

package tigase.io;

//~--- non-JDK imports --------------------------------------------------------

import tigase.stats.StatisticsList;

import tigase.util.ZLibWrapper;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import java.util.logging.Level;
import java.util.logging.Logger;

//~--- classes ----------------------------------------------------------------

/**
 * Created: Jul 29, 2009 11:58:02 AM
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class ZLibIO implements IOInterface {

	/**
	 * Variable <code>log</code> is a class logger.
	 */
	private static Logger log = Logger.getLogger(ZLibIO.class.getName());

	//~--- fields ---------------------------------------------------------------

	private IOInterface io = null;
	private ZLibWrapper zlib = null;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Constructs ...
	 *
	 *
	 * @param ioi
	 * @param level
	 */
	public ZLibIO(final IOInterface ioi, final int level) {
		this.io = ioi;
		zlib = new ZLibWrapper();
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public int bytesRead() {
		return io.bytesRead();
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	@Override
	public int getInputPacketSize() throws IOException {
		return io.getInputPacketSize();
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public SocketChannel getSocketChannel() {
		return io.getSocketChannel();
	}

	/**
	 * Method description
	 *
	 *
	 * @param list
	 */
	@Override
	public void getStatistics(StatisticsList list) {
		if (io != null) {
			io.getStatistics(list);
		}

		if (zlib != null) {
			list.add("zlibio", "Average compression rate", zlib.averageCompressionRate(), Level.FINE);
			list.add("zlibio", "Average decompression rate", zlib.averageDecompressionRate(), Level.FINE);
		}
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public boolean isConnected() {
		return io.isConnected();
	}

	/**
	 * Method description
	 *
	 *
	 * @param addr
	 *
	 * @return
	 */
	@Override
	public boolean isRemoteAddress(String addr) {
		return io.isRemoteAddress(addr);
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param buff
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	@Override
	public ByteBuffer read(ByteBuffer buff) throws IOException {
		ByteBuffer tmpBuffer = io.read(buff);

		if (io.bytesRead() > 0) {
			ByteBuffer decompressed_buff = zlib.decompress(tmpBuffer);

			// The buffer is reused to it needs to be cleared before it can be
			// used again.
			tmpBuffer.clear();

			// System.out.println("Decompression rate: " + zlib.lastDecompressionRate());
			return decompressed_buff;
		}

		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @throws IOException
	 */
	@Override
	public void stop() throws IOException {
		if (log.isLoggable(Level.FINEST)) {
			log.finest("Stop called...");
		}

		io.stop();
		zlib.end();
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "ZLIB: " + io.toString();
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public boolean waitingToSend() {
		return io.waitingToSend();
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public int waitingToSendSize() {
		return io.waitingToSendSize();
	}

	/**
	 * Method description
	 *
	 *
	 * @param buff
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	@Override
	public int write(ByteBuffer buff) throws IOException {
		if (buff == null) {
			return io.write(null);
		}

		if (log.isLoggable(Level.FINER)) {
			log.log(Level.FINER, "ZLIB - Writing data, remaining: {0}", buff.remaining());
		}

		ByteBuffer compressed_buff = zlib.compress(buff);

		// System.out.println("Compression rate: " + zlib.lastCompressionRate());
		return io.write(compressed_buff);
	}
}


//~ Formatted in Sun Code Convention


//~ Formatted by Jindent --- http://www.jindent.com
