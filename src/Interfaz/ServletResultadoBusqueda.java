package Interfaz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mundo.AplicacionWeb;
import mundo.Pedido;
import mundo.Producto;

public class ServletResultadoBusqueda extends ServletAbstract{

	@Override
	public String darTituloPagina(HttpServletRequest request) {
		return "Resultado de Busqueda";
	}

	@Override
	public void escribirContenido(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter respuesta = response.getWriter( );
		
		String criterio = request.getParameter("criterio");
				
		if (criterio.equals("buscarProductoCliente"))
		{
			String producto = request.getParameter("nombre");
			if (producto == null)
			{
				producto = request.getParameter("producto");
			}
			String login = request.getParameter("login");
			ArrayList<String> rta = new ArrayList<String>();
			try
			{
				rta = AplicacionWeb.getInstancia().buscarProducto(producto);
			}
			catch (Exception e){
				error(respuesta);
			}
			
			respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=50%>" );
			for (int i = 0; i < rta.size(); i++) {
				respuesta.write( "<form method=\"POST\" action=\"registroPedido.htm\">" );
				respuesta.write( "<tr>" );
	        	respuesta.write( "<tr><td><input alt=\"Producto\" src=\"imagenes/producto.jpg\" type=\"image\" name=\"producto\"></td>" );
	        	respuesta.write( "<td><table align=\"center\" bgcolor=\"#ecf0f1\" width=10%>" );
		        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Producto: \" name=\"label1\" style=\"border: none;\" type=\"text\"\"></h4></td><input value=\"" + producto + "\" name=\"producto\" type=\"hidden\"\"><td align=\"right\">" + producto + "</td></tr>" );
		        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Precio: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + rta.get(i) + "</td></tr>" );
		        respuesta.write( "<tr><td><table bgcolor=\"#ecf0f1\" width=10%>" );
		        respuesta.write( "<tr><td align=\"left\"><h4>Unidades: </h4></td>" );
		        respuesta.write( "<td align=\"right\">" );
		        respuesta.write( "<select size=\"1\" name=\"cantidad\" class=\"normal\" style=\"border: none;\">" );
		        respuesta.write( "<option value=\"1\">1</option>" );
		        respuesta.write( "<option value=\"2\">2</option>" );
		        respuesta.write( "<option value=\"3\">3</option>" );
		        respuesta.write( "</select>" );
		        respuesta.write( "</td></tr>" );
		        respuesta.write( "</table></td></tr>" );
		        respuesta.write( "<tr><td align=\"right\"><input value=\"Pedir\" size=\"33\" name=\"pedir\" type=\"submit\"\"></td></tr>" );
		        respuesta.write( "<input type=\"hidden\" name=\"login\" value=" + login + ">" );
		        respuesta.write( "<input type=\"hidden\" name=\"precio\" value=" + rta.get(i) + ">" );
		        respuesta.write( "</table></td>" );
		        respuesta.write( "</tr>" );
		        respuesta.write( "</form>" );
	        }
			respuesta.write( "</table>" );
		}
		
		else if (criterio.equals("darPedidos"))
		{
			String login = request.getParameter("login");
			
			ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
	        try 
	        {
	        	pedidos = AplicacionWeb.getInstancia().darPedidosCliente(login);
	        	if (pedidos.size() != 0)
		        {
	        		respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=50%>" );
			        for (Pedido ped : pedidos) {
			        	String producto = AplicacionWeb.getInstancia().buscarNombreProducto(ped.getProducto());
			        	respuesta.write( "<form method=\"POST\" action=\"registroPedido.htm\">" );
			        	respuesta.write( "<tr>" );
				        respuesta.write( "<tr><td><img alt=\"Producto\" src=\"imagenes/producto.jpg\" name=\"producto\"></td>" );
				        respuesta.write( "<td><table align=\"center\" bgcolor=\"#ecf0f1\" width=30%>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Producto Pedido: \" name=\"label1\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + producto + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Unidades Pedidas: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + ped.getCantidad() + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Fecha Pedido: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + (ped.getFechaPedido().toLocaleString()).substring(0, 10) + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Fecha Entrega: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\" size=\"\">" + (ped.getFechaEntrega().toLocaleString()).substring(0, 10) + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"right\"><input value=" + login + " size=\"53\" name=\"login\" type=\"hidden\"><input value=" + ped.getProducto() + " size=\"53\" name=\"producto\" type=\"hidden\"><input value=\"eliminarPedido\" size=\"53\" name=\"criterio\" type=\"hidden\"><input value=\"Eliminar Pedido\" size=\"53\" name=\"eliminar\" type=\"submit\"></td></tr>" );
				        respuesta.write( "</table></td>" );
				        respuesta.write( "</tr>" );
				        respuesta.write( "<tr></tr>" );
				        respuesta.write( "</form>" );
					}
			        respuesta.write( "</table>" );
		        }
	        	else
	        		noHayPedidos(login, respuesta);
	        }
	        catch (Exception e)
	        {
	        	noHayPedidos(login, respuesta);
	        }
		}
		
		else if(criterio.equals("eliminarPedido"))
		{
			String login = request.getParameter("login");
			String prodEliminar = request.getParameter("producto");
			ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
			try
			{
				AplicacionWeb.getInstancia().eliminarPedidoCliente(login, prodEliminar);
			}
			catch (Exception e)
			{
				
			}
			try 
	        {
	        	pedidos = AplicacionWeb.getInstancia().darPedidosCliente(login);
	        	if (pedidos.size() != 0)
		        {
	        		respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=50%>" );
			        for (Pedido ped : pedidos) {
			        	String producto = AplicacionWeb.getInstancia().buscarNombreProducto(ped.getProducto());
			        	respuesta.write( "<form method=\"POST\" action=\"registroPedido.htm\">" );
			        	respuesta.write( "<tr>" );
				        respuesta.write( "<tr><td><img alt=\"Producto\" src=\"imagenes/producto.jpg\" name=\"producto\"></td>" );
				        respuesta.write( "<td><table align=\"center\" bgcolor=\"#ecf0f1\" width=30%>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Producto Pedido: \" name=\"label1\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + producto + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Unidades Pedidas: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + ped.getCantidad() + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Fecha Pedido: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\">" + (ped.getFechaPedido().toLocaleString()).substring(0, 10) + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"left\"><h4><input value=\"Fecha Entrega: \" name=\"label2\" style=\"border: none;\" type=\"text\"\"></h4></td><td align=\"right\" size=\"\">" + (ped.getFechaEntrega().toLocaleString()).substring(0, 10) + "</td></tr>" );
				        respuesta.write( "<tr><td align=\"right\"><input value=" + login + " size=\"53\" name=\"login\" type=\"hidden\"><input value=" + ped.getProducto() + " size=\"53\" name=\"producto\" type=\"hidden\"><input value=\"eliminarPedido\" size=\"53\" name=\"criterio\" type=\"hidden\"><input value=\"Eliminar Pedido\" size=\"53\" name=\"eliminar\" type=\"submit\"></td></tr>" );
				        respuesta.write( "</table></td>" );
				        respuesta.write( "</tr>" );
				        respuesta.write( "<tr></tr>" );
				        respuesta.write( "</form>" );
					}
			        respuesta.write( "</table>" );
		        }
	        	else
	        		noHayPedidos(login, respuesta);
	        }
			catch (Exception e)
	        {
	        	noHayPedidos(login, respuesta);
	        }
			
		}
		
		else if (criterio.equals("buscarPedido"))
		{
			boolean pedido1 = false;
			boolean entrega1 = false;
			Date diaSolicitud = new Date();
			Date diaEntrega = new Date();
			try
			{
				int dia1 = Integer.parseInt(request.getParameter("dia1"));
				int mes1 = Integer.parseInt(request.getParameter("mes1"));
				int anio1 = Integer.parseInt(request.getParameter("anio1"));
				diaSolicitud = new Date();
				diaSolicitud.setDate(dia1);
				diaSolicitud.setMonth(mes1);
				diaSolicitud.setYear(anio1);
				pedido1 = true;
			}
			catch(Exception e){
			}
			try{
				int dia2 = Integer.parseInt(request.getParameter("dia2"));
				int mes2 = Integer.parseInt(request.getParameter("mes2"));
				int anio2 = Integer.parseInt(request.getParameter("anio2"));
				diaEntrega = new Date();
				diaEntrega.setDate(dia2);
				diaEntrega.setMonth(mes2);
				diaEntrega.setYear(anio2);
				entrega1 = true;
			}
			catch(Exception e){
				
			}
			try
			{
				ArrayList<Pedido> pedidos = AplicacionWeb.getInstancia().buscarPedidosCliente(diaSolicitud, pedido1, diaEntrega, entrega1);
		        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
		        respuesta.write( "<tr>" );
		        respuesta.write( "<td><h3>Los pedidos encontrados son los siguientes:</h3></td>" );
		        respuesta.write( "</tr>" );
		        respuesta.write( "</table>" );
		        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
		        respuesta.write( "<tr>" );
		        respuesta.write( "<td>Producto</td>" );
		        respuesta.write( "<td>Cantidad</td>" );
		        respuesta.write( "<td>Fecha Pedido</td>" );
		        respuesta.write( "<td>Fecha Entrega</td>" );
		        respuesta.write( "<td>Acciones</td>" );
		        respuesta.write( "</tr>" );
		        for (Pedido pedido : pedidos) {
			        respuesta.write( "<tr>" );
			        respuesta.write( "<td>" + pedido.getProducto() + "</td>" );
			        respuesta.write( "<td>" + pedido.getCantidad() + "</td>" );
			        respuesta.write( "<td>" + pedido.getFechaEntrega() + "</td>" );
			        respuesta.write( "<td>" + pedido.getFechaPedido() + "</td>" );
			        respuesta.write( "<form method=\"POST\" action=\"eliminarPedido.htm\"><td><input type=\"submit\" name=\"eliminar\" value=\"Eliminar Pedido\"><input type=\"hidden\" name=\"pedidoEliminado\" value=" + pedido.getProducto() + "></td></form>" );
			        respuesta.write( "</tr>" );
				}
		        respuesta.write( "</table>" );
			}
			catch (Exception e){
		        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
		        respuesta.write( "<tr>" );
		        respuesta.write( "<td><h3>El pedido buscado no existe</h3></td>" );
		        respuesta.write( "</tr>" );
		        respuesta.write( "</table>" );
			}
		}
		else if (criterio.equals("buscarAdmin"))
		{
			String materialABuscar = request.getParameter("material");
			boolean tipo = false;
			boolean rango = false;
			boolean etapa = false;
			String nombreMaterial = "";
			int menorA = 0;
			int mayorA = 0;
			String etapaProd = "";
			ArrayList<String> rta = new ArrayList<String>();
			
			try
			{
				nombreMaterial = request.getParameter("nombreMaterial");
				tipo = true;
			}
			catch (Exception e){
				
			}
			try{
				mayorA = Integer.parseInt(request.getParameter("mayorA"));
				menorA = Integer.parseInt(request.getParameter("menorA"));
				rango = true;
			}
			catch (Exception e){
				
			}
			try{
				etapaProd = request.getParameter("etapaProd");
				etapa = true;
			}
			catch (Exception e){
				
			}
			if (materialABuscar.equals("producto")){
				try{
					rta = AplicacionWeb.getInstancia().buscarExistenciasProducto(tipo, nombreMaterial, rango, mayorA, menorA, etapa);	
				}
				catch (Exception e){
			        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
			        respuesta.write( "<tr>" );
			        respuesta.write( "<td><h3>El producto buscado no existe</h3></td>" );
			        respuesta.write( "</tr>" );
			        respuesta.write( "</table>" );
				}
			}
			else if (materialABuscar.equals("materiaPrima")){
				try{
					rta = AplicacionWeb.getInstancia().buscarExistenciasMateriaPrima(tipo, nombreMaterial, rango, mayorA, menorA);	
				}
				catch (Exception e){
			        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
			        respuesta.write( "<tr>" );
			        respuesta.write( "<td>Nombre " + materialABuscar + "</td>" );
			        respuesta.write( "<td>Cantidad en inventario</td>" );
			        respuesta.write( "</tr>" );
			        for (String string : rta) {
				        respuesta.write( "<tr>" );
				        respuesta.write( "<td>" + nombreMaterial + "</td>" );
				        respuesta.write( "<td>" + string + "</td>" );
				        respuesta.write( "</tr>" );

					}
			        respuesta.write( "</table>" );
				}
			}
			else if (materialABuscar.equals("componente")){
				try{
					rta = AplicacionWeb.getInstancia().buscarExistenciasComponente(tipo, nombreMaterial, rango, mayorA, menorA);	
				}
				catch (Exception e){
			        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
			        respuesta.write( "<tr>" );
			        respuesta.write( "<td><h3>El producto buscado no existe</h3></td>" );
			        respuesta.write( "</tr>" );
			        respuesta.write( "</table>" );
				}
			}
			
	        respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
	        respuesta.write( "<tr>" );
	        respuesta.write( "<td><h3>El producto buscado no existe</h3></td>" );
	        respuesta.write( "</tr>" );
	        respuesta.write( "</table>" );
		}
	}
	
	public void noHayPedidos(String login, PrintWriter respuesta){
		respuesta.write( "<h4 align=\"center\">No has registrado ning�n pedido con nosotros, creemos que estos productos que podr�an interesarte.</h4>" );
    	ArrayList<String> productos = new ArrayList<String>();
    	try
		{
			productos = AplicacionWeb.getInstancia().darCantidadProductos(100);
			respuesta.write( "<table align=\"center\" bgcolor=\"#ecf0f1\" width=80%>" );
	        for (int i = 0; i < productos.size(); i++) {
	        	respuesta.write( "<tr>" );
	        	respuesta.write( "<form method=\"POST\" action=\"resultadoBusqueda.htm\">" );
		        respuesta.write( "<input type=\"hidden\" name=\"criterio\" value=\"buscarProductoCliente\" >" );
		        respuesta.write( "<input type=\"hidden\" name=\"login\" value=" + login + ">" );
	        	respuesta.write( "<td><input alt=\"Producto\" src=\"imagenes/producto.jpg\" type=\"image\" name=\"producto\" value=" + productos.get(i) + "></td>" );
		        respuesta.write( "<td><input value=" + productos.get(i) + " name=\"nombre\" style=\"background: #FFFFFF; border: none;\" type=\"submit\"\"></td>" );
		        respuesta.write( "</form>" );
		        try
		        {
		        	respuesta.write( "<form method=\"POST\" action=\"resultadoBusqueda.htm\">" );
		        	respuesta.write( "<input type=\"hidden\" name=\"criterio\" value=\"buscarProductoCliente\" >" );
			        respuesta.write( "<input type=\"hidden\" name=\"login\" value=" + login + ">" );
		        	respuesta.write( "<td><input alt=\"Producto\" src=\"imagenes/producto.jpg\" type=\"image\" name=\"producto\" value=" + productos.get(i + 1) + "></td>" );
			        respuesta.write( "<td><input value=" + productos.get(i+1) + " name=\"nombre\" style=\"background: #FFFFFF; border: none;\" type=\"submit\"\"></td>" );
			        respuesta.write( "</form>" );
		        }
		        catch(Exception e2){	
		        }
		        try
		        {
		        	respuesta.write( "<form method=\"POST\" action=\"resultadoBusqueda.htm\">" );
		        	respuesta.write( "<input type=\"hidden\" name=\"criterio\" value=\"buscarProductoCliente\" >" );
			        respuesta.write( "<input type=\"hidden\" name=\"login\" value=" + login + ">" );
		        	respuesta.write( "<td><input alt=\"Producto\" src=\"imagenes/producto.jpg\" type=\"image\" name=\"producto\" value=" + productos.get(i + 2) + "></td>" );
			        respuesta.write( "<td><input value=" + productos.get(i+2) + " name=\"nombre\" style=\"background: #FFFFFF; border: none;\" type=\"submit\"\"></td>" );
			        respuesta.write( "</form>" );
		        }
		        catch(Exception e3){	
		        }
		        try
		        {
		        	respuesta.write( "<form method=\"POST\" action=\"resultadoBusqueda.htm\">" );
		        	respuesta.write( "<input type=\"hidden\" name=\"criterio\" value=\"buscarProductoCliente\" >" );
			        respuesta.write( "<input type=\"hidden\" name=\"login\" value=" + login + ">" );
		        	respuesta.write( "<td><input alt=\"Producto\" src=\"imagenes/producto.jpg\" type=\"image\" name=\"producto\" value=" + productos.get(i + 3) + "></td>" );
		        	respuesta.write( "<td><input value=" + productos.get(i+3) + " name=\"nombre\" style=\"background: #FFFFFF; border: none;\" type=\"submit\"\"></td>" );
			        respuesta.write( "</form>" );
		        }
		        catch(Exception e4){	
		        }
		        respuesta.write( "</tr>" );
		        i+=4;
	        }
	        respuesta.write( "</table>" );
		}
		catch (Exception e1){
		}
	}

	public void error(PrintWriter respuesta){
		
		respuesta.write( "<table bgcolor=\"#ecf0f1\" width=80%>" );
	    respuesta.write( "<tr>" );
	    respuesta.write( "<td><h3>Oops! Hubo un error, lo sentimos, vuelve a intentarlo nuevamente.</FONT></td>" );
	    respuesta.write( "</tr>" );
	    respuesta.write( "</table>" );
			
	}
}
