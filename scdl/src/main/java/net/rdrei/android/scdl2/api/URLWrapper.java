package net.rdrei.android.scdl2.api;

import java.io.IOException;
import java.net.URLConnection;

public interface URLWrapper {

	URLConnection openConnection() throws IOException;

}