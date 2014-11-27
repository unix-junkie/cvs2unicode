/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode;

/**
 * @author <a href = "mailto:andrey.shcheglov@hp.com">Andrey Shcheglov</a>
 */
@FunctionalInterface
public interface FileProcessedListener {
	void fileProcessed();
}
