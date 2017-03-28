package v1.rest;

import exceptions.Logging;
import v1.utils.config.ConfigProperties;
import v1.utils.dump.Dump;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.json.simple.JSONObject;

@Path("/dump")
public class DumpResource {

	public static String mode = "start";
	public static String out;
	public static long startTime = System.currentTimeMillis();
	public static boolean dumping = false;
	public static String fileDir = "";
	public static String filePath = "";
	public static String tmpDirString = "tmpDir_1";
	public static String tmpDirString2 = "tmpDir_2";
	public static String tmpDirPath = "";
	public static String tmpDirPath2 = "";
	public static String downloadLink = "";
	public static String size_url = "";
	public static long sleepTimeInMills = 43200000; //3600000 = 1 hour | 43200000 = 12 hours
	public static String lastDump = "";
	public static String lastDumpTime = "";
	private static final List<String> fileList = new ArrayList<String>();
	private static int maxDumps = 100;
	public static String numberOfTriples = "";
	public static int dumbNo = -1;

	@GET
	public Response getList() {
		try {
			fileDir = ConfigProperties.getPropertyParam("dump_server");
			String html_header = "<html>";
			html_header += "<head>";
			html_header += "</head>";
			html_header += "<body>";
			String html_footer = "</body>";
			html_footer += "</html>";
			listFilesForFolder();
			out = "<h1>Index of Labeling System Data Hub Dumps</h1>";
			if (fileList.size() > 0) {
				out += "<table style=\"width:75%\" border='1'>";
				out += "<tr><th>Name</th><th>Last modified</th><th>Size</th></tr>";
				for (String file : fileList) {
					out += "<tr>"
							+ "<td width='25%'><a href='" + ConfigProperties.getPropertyParam("dump_web") + file.split("#")[0] + "'>" + file.split("#")[0] + "</a></td>"
							+ "<td width='25%'>" + file.split("#")[2] + "</td>"
							+ "<td width='25%'>" + file.split("#")[1] + " KB</td>"
							+ "</tr>";
				}
				out += "</table>";
			}
			return Response.ok(html_header + out + html_footer).header("Content-Type", "text/html;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.DumpResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

	@GET
	@Path("/repository/{repo}")
	@Produces("application/json;charset=UTF-8")
	public Response getDump(@PathParam("repo") String repo) {
		try {
			String dumpFile = Dump.writeFile(repo);
			JSONObject out = new JSONObject();
			out.put("file", dumpFile);
			return Response.ok(out).header("Content-Type", "application/json;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.DumpResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

	public void listFilesForFolder() {
		fileList.clear();
		// http://www.avajava.com/tutorials/lessons/how-do-i-sort-an-array-of-files-according-to-their-last-modified-dates.html
		File directory = new File(fileDir);
		File[] files = directory.listFiles((FileFilter) FileFileFilter.FILE);
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		for (File file : files) {
			double filesize = (file.length() / 1024);
			fileList.add(file.getName() + "#" + filesize + "#" + new Date(file.lastModified()));
		}
	}

	public static void listDumpFilesDeleteForMax() throws IOException {
		fileList.clear();
		File directory = new File(fileDir);
		File[] files = directory.listFiles((FileFilter) FileFileFilter.FILE);
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		for (File file : files) {
			if (!file.getName().contains("labelingsystem-latest")) {
				fileList.add(file.getName());
			}
		}
		boolean tmp = true;
		while (tmp) {
			if (fileList.size() >= maxDumps) {
				File fileDelete = new File(fileDir + fileList.get(fileList.size() - 1));
				Files.deleteIfExists(fileDelete.toPath());
				fileList.remove(fileList.size() - 1);
			} else {
				tmp = false;
				break;
			}
		}
	}

	public static int getNumberOfStatements() throws MalformedURLException, IOException {
		try {
			URL obj = new URL(size_url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			int size = Integer.parseInt(response.toString());
			return size;
		} catch (Exception e) {
			throw new NullPointerException();
		}
	}

}
