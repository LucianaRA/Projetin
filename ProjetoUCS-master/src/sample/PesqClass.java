package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lombok.Data;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Data
 public class
PesqClass extends ConectaBanco {

    public String Title;
    public String Isbn;
    public String Autor;
    public String Editora;
    public Integer DataInicial;
    public Integer DataFinal;

    static final String driver = "com.mysql.cj.jdbc.Driver";
    static final String url = "jdbc:mysql://localhost/biblioteca";
    static final String user = "root";
    static final String pass = "rootado";



    @SneakyThrows
    public List<ObraClass> pesquisa(ObraClass obra, TextField txtTitle,
                                    TextField txtIsbn, TextField txtActor,
                                    TextField txtEditora, TextField txtDate, TextField txtDateFinal) throws SQLException {


        PesqClass pesq = new PesqClass();

        Connection conAux = pesq.connec();

        pesq.Title = txtTitle.getText();
        pesq.Isbn = txtIsbn.getText();
        pesq.Autor = txtActor.getText();
        pesq.Editora = txtEditora.getText();
        if (txtDate.getText() != "" && txtDateFinal.getText() != ""){
            pesq.DataInicial = Integer.parseInt(txtDate.getText());
            pesq.DataFinal = Integer.parseInt(txtDateFinal.getText());
        }
        else {
            pesq.DataInicial = 0;
            pesq.DataFinal = 9999;
        }

        String sql = "SELECT * FROM obras";

        List<String> where = new ArrayList<String>() {
        };

        if(pesq.Title != ""){
            where.add("obr_nome LIKE '%" + pesq.Title + "%'");
        }
        if(pesq.Isbn != ""){
            where.add("obr_isbn LIKE '%" + pesq.Isbn + "%'");
        }
        if(pesq.Editora != ""){
            where.add("obr_editora LIKE '%" + pesq.Editora + "%'");
        }
        if(pesq.Autor != ""){
            where.add("obr_autores LIKE '%" + pesq.Autor + "%'");
        }
        if(!pesq.DataInicial.equals(null)){
            where.add("obr_anopub > " + pesq.DataInicial + "");
        }
        if(!pesq.DataFinal.equals(null)){
            where.add("obr_anopub < " + pesq.DataFinal + "");
        }

        if (where.size() > 0)
        {
            sql += " WHERE ";
        }


        for (int i = 0; i < where.size(); i++)
        {
            sql += " " + where.get(i);
            if (i != where.size() - 1)
                sql += " AND";

        }

        sql += " order by obr_nome ASC ";

        Statement st = conAux.createStatement();
        ResultSet rs = st.executeQuery(sql);
        //rs = st.executeQuery(sql);
        ListaLivros<ObraClass> listinha = new ListaLivros<>();
        try {
            while (rs.next()) {

                ObraClass item = new ObraClass();

                item.Id = rs.getInt("obr_id");
                item.Titulo = rs.getString("obr_nome");
                item.Isbn = rs.getString("obr_isbn");
                item.Autores = rs.getString("obr_autores");
                item.Editora = rs.getString("obr_editora");
                item.Lanc = rs.getInt("obr_anopub");

                listinha.IncluirNoInicio(item);
            }


            if (st != null) {
                st.close();
                conAux.close();
            }


            if (listinha.size() <= 0) {
                throw new ExcecaoDeLivroNaoEncontrado(listinha);
            }
        }
        catch (ExcecaoDeLivroNaoEncontrado e){
            e.getMessage();
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.toString() + " \n" + "Livro não encontrado\n");
            alert.setContentText("Algo deu errado e não foi possível localizar o livro");

            alert.showAndWait();
        }


        return listinha;

    }

}
