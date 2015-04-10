package Interfaz;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.security.util.Password;
import mundo.AplicacionWeb;

public class ServletRegistrar extends ServletAbstract{

	@Override
	public String darTituloPagina(HttpServletRequest request) {
		return "Bienvenido";
	}

	@Override
	public void escribirContenido(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try
		
		{
			PrintWriter respuesta = response.getWriter( );
				
			String usuario = request.getParameter("usuario");
			String contrasenia = request.getParameter("contrasenia");
			String tipo = request.getParameter("tipo");
			String tipoUsuario;
			
			try
			{
				AplicacionWeb.getInstancia().registrarUsuario(usuario, contrasenia, tipo);
				aceptarIngreso(respuesta, usuario, tipo);
			}
			catch(Exception e)
			{
				denegarRegistro(respuesta);
			}
		
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public void aceptarIngreso(PrintWriter respuesta, String login, String tipo){
		
		if (tipo.equals("admin"))
		{
	        respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=80%>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td><h3> BIENVENIDO " + tipo.toUpperCase() + ": " + login + "</h3></td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "</table>" );
			respuesta.write( "<div></div>" );
	        respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=80%>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td>Registrar Proveedor</td>" );
	        respuesta.write( "<td><form method=\"POST\" action=\"registrarProveedor.htm\"><input type=\"submit\" value=\"Registrar\" name=\"regProve\" class=\"normal\"></form></td>" );
	        respuesta.write( "<td>.</td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td>Registrar Llegada de Material</td>" );
	        respuesta.write(" <form method=\"POST\" action=\"registroLlegadaMaterial.htm\"><td>");
	        respuesta.write(" <select name=\"llegadaMaterial\"> class=\"normal\"");
	        respuesta.write(" <option value=\"materiaPrima\">Materia Prima</option> ");
	        respuesta.write(" <option value=\"componente\">Componente</option> ");
	        respuesta.write(" </select></td>");
	        respuesta.write(" <td><input type=\"submit\" value=\"Registrar\" name=\"B1\" class=\"normal\">");
	        respuesta.write(" </form>");
	        respuesta.write( "</tr>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td>Registrar Producto</td>" );
	        respuesta.write( "<td><form method=\"POST\" action=\"registrarProducto.htm\"><input type=\"submit\" value=\"Registrar Producto\" name=\"regProd\" class=\"normal\"></form></td>" );
	        respuesta.write( "<td>.</td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td><a href=\"buscarExistencias.htm\">Buscar Existencias de Materiales</a></td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "</table>" );
		}
		else
		{
	        respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=80%>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td><h3> BIENVENIDO " + tipo.toUpperCase() + ": " + login + "</h3></td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "</table>" );
			respuesta.write( "<div></div>" );
	        respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=80%>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td><form method=\"POST\" action=\"resultadoBusqueda.htm\">Encuentra el producto que estas buscando</td>" );
	        respuesta.write( "<td><input type=\"text\" name=\"nombre\" class=\"normal\"></td>" );
	        respuesta.write( "<input type=\"hidden\" value=\"buscarProductoCliente\" name=\"criterio\" class=\"normal\">" );
	        respuesta.write( "<td><input type=\"submit\" value=\"Buscar Productos\" name=\"bus\" class=\"normal\"></form></td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td><a href=\"buscarPedido.htm\">Consulta los detalles de tus pedidos</a></td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "</table>" );
		}
		
	}
	
	public void denegarRegistro(PrintWriter respuesta){
		
        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
        respuesta.write( "<tr>" );
        respuesta.write( "<td><h3>Error: Ya existe un usuario con ese login</h3></td>" );
        respuesta.write( "</tr>" );
        respuesta.write( "</table>" );
		
	}

}
