/////
/*
 *
 * Sumedh Saurabh
 * Ryan Cardin
 * Anh Tran
 *
 */
////

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXML;

import javax.swing.*;
import javax.xml.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.awt.*;
import java.io.*;
import java.lang.Object.*;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Controller {


    public Controller() throws SQLException {

    }

    public void change_page(Button b, String page) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource(page));
        Stage stage = (Stage)b.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private ListView listFile;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button btnLogin, btnRegister, btnLoginSubmit, btnBack, btnRegSubmit, btnClose;

    @FXML
    private TextField txtUser, txtPassword, txtFName, txtRegUser, txtRegPassword, txtRegEmail, txtLName;

    @FXML
    private Label pVal;

    @FXML
    private String filename = "src\\sample\\account.xml";
    private File list;
    private ObservableList<File> fileObservableList;

    private String filelist = "src\\sample\\files";

    @FXML
    protected void uploadFile(ActionEvent event) throws IOException {
        FileChooser fileSelection = new FileChooser();
        fileSelection.setTitle("Open File");
        Stage stage = (Stage)anchor.getScene().getWindow();
        File file = fileSelection.showOpenDialog(stage);
        fileObservableList.add(file);

        String fileTempName = file.getName();
        listFile.setItems(fileObservableList);

        InputStream inStream = null;
        OutputStream outStream = null;

        File dest = new File("src\\sample\\files\\" + file.getName());
        /*try{
        Files.copy(file,dest, REPLACE_EXISTING);
        } catch(IOException e){
            e.printStackTrace();
        }
        */

        inStream = new FileInputStream(file);
        outStream = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];


        int fileLength;
        while ((fileLength = inStream.read(buffer)) > 0){

            outStream.write(buffer, 0, fileLength );

        }

        inStream.close();
        outStream.close();

        if(file != null){
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);

        }
    }

    @FXML
    protected void initialize(){
        try {
            list = new File(filelist);
            File[] folder = list.listFiles();
            fileObservableList = FXCollections.observableArrayList();
            for (int i = 0; i < folder.length; i++) {
                if (folder[i].isFile()) {
                    fileObservableList.add(folder[i]);
                } else if (folder[i].isDirectory()) {
                    System.out.println("It is a Folder!");
                }
            }
            listFile.setItems(fileObservableList);
        } catch(NullPointerException n){
            System.out.println("No files!");
        }
    }

    @FXML
    protected void close(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    protected void reg_click(ActionEvent event) throws IOException {
        change_page(btnRegister,"register.fxml");
    }

    @FXML
    protected void login_click(ActionEvent event) throws IOException {
        change_page(btnLogin,"login.fxml");
    }

    @FXML
    protected void back_click(ActionEvent event) throws IOException {
        change_page(btnBack,"sample.fxml");
    }

    public List<Account> parseXML(List<Account> accList){
        File xmlFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(String.valueOf(xmlFile));
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Account"); // receiving both current entrees as null values.. Fix so it reads value. Otherwise, method is working.
            //now XML is loaded as Document in memory, lets convert it to Object List
            //Test
            for (int i = 0; i < nodeList.getLength(); i++) {
                System.out.println(getAccount(nodeList.item(i))); // Error Starts here
            }
            //

            for (int i = 0; i < nodeList.getLength(); i++) {
                accList.add(getAccount(nodeList.item(i))); // Error Starts here
            }
            //lets print Employee list information
            for (Account temp : accList) {
                System.out.println(temp.toString());
            }
        } catch (ParserConfigurationException | IOException | SAXException e1) {
            e1.printStackTrace();
        }

        return accList;
    }
    public void validateUser(ActionEvent event) throws IOException{
        List<Account> accList = new ArrayList<>();
        accList = parseXML(accList);

        Boolean isSuccess = false;

        Account result = accList.parallelStream()
                .filter(a -> Objects.equals(a.getUsername(), txtUser.getText()))
                .findAny()
                .orElse(null);
        if(result.getPassword().equals(txtPassword.getText()))
            isSuccess = true;
        if(isSuccess==true) {
            System.out.println("This is working");
            change_page(btnLoginSubmit, "Menu.fxml");

        } else {
            System.out.println(result.getPassword() + "\n" + txtPassword.getText());
            System.out.println("This is nt working");
            Popup errorLogin = new Popup();
            Label label = new Label("Invalid Login! Try again!");
            errorLogin.getContent().add(label);
            txtUser.setText("Invalid Login! Try Again!");
        }
    }
    public boolean validatePassword(String pass){
        if(pass.length()<8){
            pVal.setText("Your password must be atleast 8 characters long");
            return false;
        }
        Pattern p = Pattern.compile( "[0-9]" );
        Matcher m = p.matcher( pass );
        if(m.find()){
            System.out.println("You all good man!");
        } else {
            pVal.setText("Your password must contain a number");
            return false;
        }
        Pattern l = Pattern.compile("[a-zA-Z]+");
        m = l.matcher(pass);
        if (!m.find()){
            pVal.setText("Your password must contain a letter");
            return false;
        }
        return true;
    }

    public Account viewUser(String name, boolean admin){
        Account user = new Account();

        return user;
    }
    private static Account getAccount(Node node) {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        Account acc = new Account();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            acc.setFName(getTagValue("fname", element));     // will not accept method
            acc.setLName(getTagValue("lname", element));
            acc.setEmail(getTagValue("email", element));
            acc.setPassword(getTagValue("password", element));
            acc.setUsername(getTagValue("user", element));
        }

        return acc;
    }
    private static String getTagValue(String tag, Element element) {
        //String value = element.getElementsByTagName(tag).item(0).getTextContent();
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes(); // Error caused by this line. There's a null pointer exception
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue(); //value;
    }

    @FXML
    protected void validateReg(ActionEvent e) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Account acc1 = new Account();
        boolean isSuccess = false;

        List<Account> temp = new ArrayList<>();
        temp = parseXML(temp);

        acc1.setFName(txtFName.getText());
        acc1.setLName(txtLName.getText());
        acc1.setEmail(txtRegEmail.getText());
        acc1.setPassword(txtRegPassword.getText());
        acc1.setUsername(txtRegUser.getText());
        File xmlFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(filename);

        Element root = document.getDocumentElement();
        Element rootElement = document.getDocumentElement();

        List<Account> acc = new ArrayList<Account>();
        acc.add(acc1);

        Account res = temp.parallelStream()
                .filter(a -> Objects.equals(a.getUsername(), txtRegUser.getText()))
                .findAny()
                .orElse(null);
        try {
            if (acc1.getUsername().equals(res.getUsername())) {
                System.out.println("This is not correct");
            } else {
                System.out.println("It's correct");
            }
        } catch(NullPointerException n){
            if(validatePassword(txtRegPassword.getText())) {
                for (Account i : acc) {
                    Element account = document.createElement("Account");
                    rootElement.appendChild(account);

                    Element fname = document.createElement("fname");
                    fname.appendChild(document.createTextNode(i.getfName()));
                    account.appendChild(fname);

                    Element lname = document.createElement("lname");
                    lname.appendChild(document.createTextNode(i.getlName()));
                    account.appendChild(lname);

                    Element email = document.createElement("email");
                    email.appendChild(document.createTextNode(i.getEmail()));
                    account.appendChild(email);

                    Element password = document.createElement("password");
                    password.appendChild(document.createTextNode(i.getPassword()));
                    account.appendChild(password);

                    Element username = document.createElement("user");
                    username.appendChild(document.createTextNode(i.getUsername()));
                    account.appendChild(username);

                    root.appendChild(account);
                }

                DOMSource source = new DOMSource(document);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StreamResult result = new StreamResult(filename);
                transformer.transform(source, result);
                change_page(btnRegSubmit, "Menu.fxml");
            }

        }

    }
}
