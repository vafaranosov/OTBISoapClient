/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientservlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import oracle.bi.web.soap.QueryResults;
import oracle.bi.web.soap.ReportParams;
import oracle.bi.web.soap.ReportRef;
import oracle.bi.web.soap.SAWSessionService;
import oracle.bi.web.soap.SAWSessionServiceSoap;
import oracle.bi.web.soap.XMLQueryExecutionOptions;
import oracle.bi.web.soap.XMLQueryOutputFormat;
import oracle.bi.web.soap.XmlViewService;
import oracle.bi.web.soap.XmlViewServiceSoap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author larson
 */
@WebServlet(name = "OTBIWSServlet", urlPatterns = {"/OTBIWSServlet"})
public class OTBIWSServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParserConfigurationException, SAXException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String reportName = request.getParameter("reportName");
            String userName = request.getParameter("userName");
            String userPass = request.getParameter("userPass");

            //Initialize WS operation arguments
            SAWSessionService service = new SAWSessionService();
            SAWSessionServiceSoap port = service.getSAWSessionServiceSoap();
            String sessionId = port.logon(userName, userPass);
            System.out.println("SessionID " + sessionId);

            XmlViewService xmlservice = new XmlViewService();
            XmlViewServiceSoap xmlport = xmlservice.getXmlViewServiceSoap();

            ReportRef report = new ReportRef();
            report.setReportPath(reportName);

            XMLQueryOutputFormat outputFormat = XMLQueryOutputFormat.SAW_ROWSET_SCHEMA_AND_DATA;
            XMLQueryExecutionOptions executionOptions = new XMLQueryExecutionOptions();
            executionOptions.setMaxRowsPerPage(1);
            executionOptions.setPresentationInfo(true);
            ReportParams reportParams = new ReportParams();
            QueryResults results;

            results = xmlport.executeXMLQuery(
                    report,
                    outputFormat,
                    executionOptions,
                    reportParams,
                    sessionId);

           
            port.logoff(sessionId);

            out.println("<html>");
            out.println("<head>");

            //Display the report's name as a title in the browser's titlebar:
            out.println("<title>OTBI Report</title>");
            out.println("</head>");
            out.println("<body>");

            //Display the report's name as a header within the body of the report:
            out.println("<h2><font color='red'>OTBI Report</font></h2>");

            
            out.println("<hr><b>Your report:</b> \"" + reportName + "\"" + "<p>");

            
            out.println("<hr>");

           
            String rowset = results.getRowset();
            
            out.println("<pre lang = \"xml\">");
            out.println(rowset);
            out.println("</pre>");
            out.println("</font>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(OTBIWSServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(OTBIWSServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
