package mundo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AplicacionWeb {

	//--------------------------------------------------
	// ATRIBUTOS
	//--------------------------------------------------

	public static final String ID = "generadorId";

	public static final String[] COLUMNAS = {"id"};

	public static final String[] TIPO = {"String"};

	private static CRUD crud;

	private static ConexionDAO conexion;

	private static AplicacionWeb instancia ;

	private String usuarioActual;

	private int contadorId;


	//--------------------------------------------------
	// CONSTRUCTOR E INSTANCIACION
	//--------------------------------------------------

	public static AplicacionWeb getInstancia() {		
		if(instancia == null){
			instancia = new AplicacionWeb(); 
		}
		return instancia;
	}

	public AplicacionWeb() {
		conexion = new ConexionDAO();
		conexion.iniciarConexion();
		//conexion.crearTablas();
		crud = new CRUD(conexion);
		//poblarTablas();
		try
		{
			Statement s = crud.darConexion().createStatement();
			String sql = "SELECT MAX(id) FROM generadorId";
			System.out.println(sql);
			ResultSet rs = s.executeQuery(sql);
			while(rs.next())
			{
				contadorId = Integer.parseInt(rs.getString(1));
				System.out.println("El id actual es: " + contadorId);
			}
		}
		catch (Exception e){
			contadorId = 1004;
		}
		usuarioActual = "";
	}

	//--------------------------------------------------
	// GETTERS AND SETTERS
	//--------------------------------------------------

	public int darContadorId(){
		try
		{
			String[] id = {Integer.toString(++contadorId)};
			crud.insertarTupla(ID, COLUMNAS, TIPO, id);
			return contadorId;
		}
		catch (Exception e)
		{
			return ++contadorId;
		}
	}

	public static CRUD getCrud() {
		return crud;
	}

	public static void setCrud(CRUD crud) {
		AplicacionWeb.crud = crud;
	}

	public static ConexionDAO getConexion() {
		return conexion;
	}

	public static void setConexion(ConexionDAO conexion) {
		AplicacionWeb.conexion = conexion;
	}

	public static void setInstancia(AplicacionWeb instancia) {
		AplicacionWeb.instancia = instancia;
	}
	
	//--------------------------------------------------
	// METODOS AUXILIARES
	//--------------------------------------------------
	
	/**
	 * 
	 */
	public static void poblarTablas(){
		crud.poblarTablas();	
	}

	//--------------------------------------------------
	// METODOS CREAR
	//--------------------------------------------------
	
	/**
	 * @param login
	 * @param password
	 * @param tipo
	 * @throws Exception
	 */
	public void registrarUsuario (String login, String password, String tipo) throws Exception{
		//		columnas de Usuario: login, password, tipo, nombre, direccion, telefono, ciudad, idRepLegal
		String[] datos = {login, password, tipo, "", "", Integer.toString(0), "", ""}; 
		crud.insertarTupla(Usuario.NOMBRE, Usuario.COLUMNAS, Usuario.TIPO, datos);
		usuarioActual = login;
	}
	
	/**
	 * 
	 * @param login
	 * @param nombre
	 * @param direccion
	 * @param telefono
	 * @param ciudad
	 * @param idRepLegal
	 * @throws Exception
	 */
	public void registrarCliente (String login, String nombre, String direccion, int telefono, String ciudad, String idRepLegal) throws Exception{
		System.out.println("El usuario " + login + "ingresa por primera vez. " + nombre + direccion + telefono + ciudad + idRepLegal);
		String sql = "UPDATE " + Usuario.NOMBRE + " SET nombre = '" + nombre + "', direccion = '" + direccion + "', telefono = " + telefono + ", ciudad = '" + ciudad + "', idRepLegal = '" + idRepLegal + "' WHERE login = '" + login + "'";
		System.out.println(sql);
		crud.darConexion().createStatement().executeQuery(sql);
	}
	
	/**
	 * @param idProveedor
	 * @param direccion
	 * @param telefono
	 * @param ciudad
	 * @param idRepLegal
	 * @throws Exception
	 */
	public void registrarProveedor (String idProveedor, String direccion, int telefono, String ciudad, String idRepLegal) throws Exception{
		String[] id = {idProveedor};
		String[] datosSimples = {id[0],direccion, Integer.toString(telefono) ,ciudad,idRepLegal};
		crud.insertarTupla(Proveedor.NOMBRE, Proveedor.COLUMNAS, Proveedor.TIPO, datosSimples);
	}

	/**
	 * @param id
	 * @param unidadMedida
	 * @param cantidadInicial
	 * @throws Exception
	 */
	public void registrarMateriaPrima (String id, String unidadMedida, int cantidadInicial, String idProveedor) throws Exception{
		String[] datosSimples = {id, unidadMedida, Integer.toString(cantidadInicial)};
		int cantidadActual = cantidadInicial;
		try{
			ArrayList<String> resultado = new ArrayList<String>();
			resultado = crud.darSubTabla(Proveedor.NOMBRE_RELACION_MATERIA_PRIMA, "id_proveedor", "id_proveedor = '" + idProveedor + "' AND id_materiaPrima = '" + id + "'");
			if (resultado.isEmpty()){
				String[] datosRelacion = {idProveedor,id}; 
				crud.insertarTupla(Proveedor.NOMBRE_RELACION_MATERIA_PRIMA, Proveedor.COLUMNAS_RELACION_MATERIA_PRIMA, Proveedor.TIPO_RELACION_MATERIA_PRIMA, datosRelacion);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		try{
			cantidadActual+= Integer.parseInt((crud.darSubTabla(MateriaPrima.NOMBRE, "cantidadInicial", "id='"+id+"'").get(0)));
			String[] columnas = new String[1];
			columnas[0] = "cantidadInicial";
			String[] cantidad = new String[1];
			cantidad[0] = (Integer.toString(cantidadInicial + cantidadActual));
			crud.actualizarTupla(MateriaPrima.NOMBRE,columnas,cantidad, "id= '"+id+"'");
		}
		catch(Exception e){
			crud.insertarTupla(MateriaPrima.NOMBRE, MateriaPrima.COLUMNAS, MateriaPrima.TIPO, datosSimples);
		}
		for (int i = 0; i < cantidadInicial; i++) {
			String[] datosRegistro = {Integer.toString(darContadorId()), id};
			crud.insertarTupla(MateriaPrima.NOMBRE_REGISTRO_MATERIAS_PRIMAS, MateriaPrima.COLUMNAS_REGISTRO_MATERIAS_PRIMAS, MateriaPrima.TIPO_REGISTRO_MATERIAS_PRIMAS, datosRegistro);
		}
	}

	/**
	 * @param id
	 * @param cantidadInicial
	 * @throws Exception
	 */
	public void registrarComponente (String id, int cantidadInicial, String idProveedor) throws Exception {
		String[] datosSimples = {id, Integer.toString(cantidadInicial)};
		int cantidadActual = cantidadInicial;
		try{
			ArrayList<String> resultado = new ArrayList<String>();
			resultado = crud.darSubTabla(Proveedor.NOMBRE_RELACION_COMPONENTE, "id_proveedor", "id_proveedor = '" + idProveedor + "' AND id_componente = '" + id + "'");
			if (resultado.isEmpty()){
				String[] datosRelacion = {idProveedor,id}; 
				crud.insertarTupla(Proveedor.NOMBRE_RELACION_COMPONENTE, Proveedor.COLUMNAS_RELACION_COMPONENTE, Proveedor.TIPO_RELACION_COMPONENTE, datosRelacion);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		try{
			cantidadActual+= Integer.parseInt((crud.darSubTabla(Componente.NOMBRE, "cantidadInicial", "id="+id).get(0)));
			String[] columnas = new String[1];
			columnas[0] = "cantidadInicial";
			String[] cantidad = new String[1];
			cantidad[0] = (Integer.toString(cantidadInicial + cantidadActual));
			crud.actualizarTupla(MateriaPrima.NOMBRE,columnas,cantidad, "id="+id);
		}
		catch(Exception e){
			crud.insertarTupla(Componente.NOMBRE, Componente.COLUMNAS, Componente.TIPO, datosSimples);		}
		for (int i = 0; i < cantidadInicial; i++) {
			String[] datosRegistro = {Integer.toString(darContadorId()), id};
			crud.insertarTupla(MateriaPrima.NOMBRE_REGISTRO_MATERIAS_PRIMAS, MateriaPrima.COLUMNAS_REGISTRO_MATERIAS_PRIMAS, MateriaPrima.TIPO_REGISTRO_MATERIAS_PRIMAS, datosRegistro);
		}
	}

	/**
	 * @param id
	 * @param nombre
	 * @param precio
	 * @throws Exception
	 */
	public void registrarProducto (String id, String nombre, int precio) throws Exception{
		String[] id1 = {id};
		String[] datos = {id, nombre, Integer.toString(precio)};
		crud.insertarTupla(Producto.NOMBRE, Producto.COLUMNAS, Producto.TIPO, datos);
		System.out.println("Se registro " + datos);
	}

	/**
	 * @param id
	 * @param idProducto
	 * @param idEstacion
	 * @param idMateriaPrima
	 * @param idComponente
	 * @param duracion
	 * @param numeroSecuencia
	 * @param idAnterior
	 * @throws Exception
	 */
	public void registrarEtapaProduccion  (String id, String idProducto, String idEstacion, String idMateriaPrima, String idComponente, int duracion, int numeroSecuencia, String idAnterior) throws Exception{
		String[] datos = {id, idProducto, idEstacion, idMateriaPrima, idComponente, Integer.toString(duracion), Integer.toString(numeroSecuencia), idAnterior};
		crud.insertarTupla(Etapa.NOMBRE, Etapa.COLUMNAS, Etapa.TIPO, datos);
	}
	
	/**
	 * @param login
	 * @param producto
	 * @param cantidad
	 * @param pedido
	 * @param entrega
	 * @throws Exception
	 */
	public Date registrarPedido (String login, String idProducto, int cantidad, Date fechaPedido) throws Exception{
		System.out.println(login + " - " + idProducto + " - " + cantidad + " - " + fechaPedido.toLocaleString());
		ArrayList<Etapa> etapas = new ArrayList<Etapa>();
		String idPedido = Integer.toString(darContadorId());
		
		conexion.setAutoCommitFalso();
		Savepoint save = conexion.darConexion().setSavepoint();
		
		etapas = obtenerEtapas(idProducto);
		Date fechaEntrega = null;
		String[] datosPedido = {idPedido,idProducto,login,Integer.toString(fechaPedido.getDay()),Integer.toString(fechaPedido.getMonth()),Integer.toString(fechaPedido.getDay()),Integer.toString(fechaPedido.getDay()),Integer.toString(cantidad)};
		crud.insertarTupla(Pedido.NOMBRE, Pedido.COLUMNAS, Pedido.TIPO, datosPedido);
		
		ArrayList<String> idInventarios = new ArrayList<String>();
		for(int i = 0; i < cantidad; i++){
			try{
			idInventarios.add(Integer.toString(darContadorId()));
			String[] datosInventario = {idInventarios.get(i),idProducto,idPedido};
			crud.insertarTupla(Producto.NOMBRE_INVENTARIO_PRODUCTOS, Producto.COLUMNAS_INVENTARIO_PRODUCTOS, Producto.TIPO_INVENTARIO_PRODUCTOS, datosInventario);
			}
			catch(Exception e){
				conexion.darConexion().rollback(save);
				e.printStackTrace();
			}
		}
		for(Etapa etapa : etapas){
			try{
				fechaEntrega = verificarExistencias(idProducto,etapa,cantidad,etapas.size(),idPedido,idInventarios);
			}
			catch(Exception e){
				conexion.darConexion().rollback(save);
				e.printStackTrace();
			}
		}
		conexion.darConexion().commit();
		conexion.setAutoCommitVerdadero();
		return fechaEntrega;
	}	
	
	/**
	 * @param idProducto
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Etapa> obtenerEtapas (String idProducto) throws Exception{
		ArrayList<Etapa> etapas = new ArrayList<Etapa>();
		ResultSet rs_etapas = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Etapa.NOMBRE + " WHERE idProducto = '" + idProducto + "'");
		while(rs_etapas.next()){
			String idEtapa = rs_etapas.getString(1);
			String nombre = rs_etapas.getString(2);
			String idEstacion = rs_etapas.getString(4);
			String idMateriaPrima = rs_etapas.getString(5);
			String idComponente = rs_etapas.getString(6);
			int duracion = rs_etapas.getInt(7);
			int numeroSecuencia = rs_etapas.getInt(8);
			String idAnterior = rs_etapas.getString(9);
			Etapa etapa = new Etapa(idEtapa, nombre, idProducto, idEstacion, idMateriaPrima, idComponente, duracion, numeroSecuencia, idAnterior);
			etapas.add(etapa);
			System.out.println(etapa.toString());
		}
		return etapas;
	}
	
	
	/**
	 * @param idProducto
	 * @param etapa
	 * @param cantidad
	 * @param ultimaEtapa
	 * @param login
	 * @param fechaPedido
	 * @param idPedido
	 * @return
	 * @throws Exception
	 */
	public Date verificarExistencias (String idProducto, Etapa etapa, int cantidad, int ultimaEtapa, String idPedido, ArrayList<String> idInventarios) throws Exception{
		
		String verificarEstacionesText = "SELECT a.id AS idRegistroEstacion FROM " + Estacion.NOMBRE_REGISTRO_ESTACIONES + " a INNER JOIN " + Estacion.NOMBRE + " b ON a." + Estacion.COLUMNAS_REGISTRO_ESTACIONES[1] + "=b." + Estacion.COLUMNAS[0] + " WHERE b.tipo = '" + etapa.getIdEstacion() + "' AND NOT EXISTS (SELECT c.id FROM " + Producto.NOMBRE_REGISTRO_PRODUCTOS + " c WHERE idRegistroEstacion = a.id) ORDER BY a.dia,a.mes";
		System.out.println(verificarEstacionesText);
		ResultSet rs_verificarEstaciones = crud.darConexion().createStatement().executeQuery(verificarEstacionesText);
		
		String verificarMateriasPrimasText = "SELECT a.id FROM " + MateriaPrima.NOMBRE_REGISTRO_MATERIAS_PRIMAS + " a WHERE a.idMateriaPrima = '" + etapa.getIdMateriaPrima() + "' AND NOT EXISTS (SELECT b.id FROM " + Producto.NOMBRE_REGISTRO_PRODUCTOS + " b WHERE idRegistroMateriaPrima = a.id)";
		System.out.println(verificarMateriasPrimasText);
		ResultSet rs_verificarMateriasPrimas =  crud.darConexion().createStatement().executeQuery(verificarMateriasPrimasText);

		String verificarComponentesText = "SELECT a.id FROM " + Componente.NOMBRE_REGISTRO_COMPONENTES + " a WHERE a.idComponente = '" + etapa.getIdComponente() + "' AND NOT EXISTS (SELECT b.id FROM " + Producto.NOMBRE_REGISTRO_PRODUCTOS + " b WHERE idRegistroComponente = a.id)";
		System.out.println(verificarComponentesText);
		ResultSet rs_verificarComponentes =  crud.darConexion().createStatement().executeQuery(verificarComponentesText);
		
		Date fechaEntrega = null;

		for(int i = 0; i < cantidad; i++){
			rs_verificarEstaciones.next();
			rs_verificarMateriasPrimas.next();
			rs_verificarComponentes.next();
			
			String idRegProd = Integer.toString(darContadorId());
			String[] datosRegProd = {idRegProd,etapa.getId(),idInventarios.get(i),rs_verificarEstaciones.getString(1),rs_verificarMateriasPrimas.getString(1),rs_verificarComponentes.getString(1)};
			crud.insertarTupla(Producto.NOMBRE_REGISTRO_PRODUCTOS, Producto.COLUMNAS_REGISTRO_PRODUCTOS, Producto.TIPO_REGISTRO_PRODUCTOS, datosRegProd);
			
			if(etapa.getNumeroSecuencia() == ultimaEtapa){
				if(i==cantidad-1){
					String hallarFechaEntregaText = "SELECT a.dia, a.mes FROM " + Estacion.NOMBRE_REGISTRO_ESTACIONES + " a WHERE a.id = '" + rs_verificarEstaciones.getString(1) + "'";
					ResultSet rs_hallarFechaEntrega =  crud.darConexion().createStatement().executeQuery(hallarFechaEntregaText);
					rs_hallarFechaEntrega.next();				
					int diaEntrega = Integer.parseInt(rs_hallarFechaEntrega.getString(1));
					int mesEntrega = Integer.parseInt(rs_hallarFechaEntrega.getString(2));
					Calendar calendario = Calendar.getInstance();
					calendario.setTime(new Date(2015, mesEntrega, diaEntrega));
					calendario.add(Calendar.DATE, 1);
					fechaEntrega = calendario.getTime();
					String actualizarFechaEntrega = "UPDATE pedidos SET diaEntrega = " + Integer.toString(fechaEntrega.getDay()) + ", mesEntrega = " + Integer.toString(fechaEntrega.getMonth()) + " WHERE id = '" + idPedido + "'";
					crud.darConexion().createStatement().executeQuery(actualizarFechaEntrega);
				}
			}
		}
		return fechaEntrega;
	}
	
	
	
	//--------------------------------------------------
	// METODOS DAR Y BUSCAR
	//--------------------------------------------------

	/**
	 * @param login
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String buscarUsuario (String login, String password) throws Exception{
		ArrayList<String> usuario = crud.darSubTabla(Usuario.NOMBRE, "tipo", "login = '" + login + "' AND password = '" + password + "'");
		usuarioActual = login;
		if ( usuario.get(0) != null )
			return usuario.get(0);
		return "";
	}
	
	/**
	 * @param id
	 * @param idDeseado
	 * @param rango
	 * @param mayorA
	 * @param menorA
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> buscarMateriaPrima (boolean id, String idDeseado, boolean rango, int mayorA, int menorA) throws Exception{
		if(id && rango){
			return crud.darSubTabla (MateriaPrima.NOMBRE, "cantidadInicial", "id = " + idDeseado + " AND cantidadInicial BETWEEN " + mayorA + " AND " + menorA);				
		}
		else if(id){
			return crud.darSubTabla (MateriaPrima.NOMBRE, "cantidadInicial", "id = " + idDeseado);				
		}
		else if(rango){
			return crud.darSubTabla (MateriaPrima.NOMBRE, "cantidadInicial", "cantidadInicial BETWEEN " + mayorA + " AND " + menorA);				
		}
		return null;
	}

	/**
	 * @param nombre
	 * @param nombreDeseado
	 * @param rango
	 * @param mayorA
	 * @param menorA
	 * @param etapa
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> buscarProducto (boolean nombre, String nombreDeseado, boolean rango, int mayorA, int menorA, boolean etapa) throws Exception{
		if(nombre && rango && etapa){
			return crud.darSubTabla (Producto.NOMBRE, "cantidad", "nombre = " + nombre + " AND cantidad BETWEEN " + mayorA + " AND " + menorA);				
		}
		else if(nombre && rango){
			return crud.darSubTabla (Producto.NOMBRE, "cantidad", "nombre = " + nombre + " AND cantidad BETWEEN " + mayorA + " AND " + menorA);				
		}
		else if(nombre){
			return crud.darSubTabla (Producto.NOMBRE, "cantidad", "nombre = " + nombre);				
		}
		else if(rango){
			return crud.darSubTabla (Producto.NOMBRE, "cantidad", "cantidad BETWEEN " + mayorA + " AND " + menorA);				
		}
		return null;
	}

	/**
	 * @param id
	 * @param idDeseado
	 * @param rango
	 * @param mayorA
	 * @param menorA
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> buscarComponente (boolean id, String idDeseado, boolean rango, int mayorA, int menorA) throws Exception{
		if(id && rango){
			return crud.darSubTabla (Componente.NOMBRE, "cantidadInicial", "id = " + idDeseado + " AND cantidadInicial BETWEEN " + mayorA + " AND " + menorA);				
		}
		else if(id){
			return crud.darSubTabla (Componente.NOMBRE, "cantidadInicial", "id = " + idDeseado);				
		}
		else if(rango){
			return crud.darSubTabla (Componente.NOMBRE, "cantidadInicial", "cantidadInicial BETWEEN " + mayorA + " AND " + menorA);				
		}
		return null;
	}

	/**
	 * @param nombre
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Producto> buscarProducto (String nombre) throws Exception{
		ArrayList<Producto> rta = new ArrayList<Producto>();
		ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT DISTINCT * FROM " + Producto.NOMBRE + " WHERE UPPER(nombre) LIKE UPPER('%" + nombre + "%')");
		while(rs.next())
		{
			String id = rs.getString(1);
			String pNombre = rs.getString(2);
			int precio = rs.getInt(3);
			Producto producto = new Producto(id, pNombre, precio);
			rta.add(producto);
		}
		return rta;
	}

	/**
	 * @param pedido
	 * @param pedido1
	 * @param entrega
	 * @param entrega1
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Pedido> darPedidos (String condicionPedido, String condicionNombreCliente, String condicionLoginCliente, String condicionProducto) throws Exception{
		ArrayList<Pedido> rta = new ArrayList<Pedido>();
		String sql = "SELECT * FROM (SELECT user1.login AS login, user1.nombre AS nombreCliente, proPed.id AS idPedido, proPed.nombre AS nombreProducto, proPed.cantidad AS cantidad, proPed.diaPedido AS diaPedido, proPed.mesPedido AS mesPedido, proPed.diaEntrega AS diaEntrega, proPed.mesEntrega AS mesEntrega FROM usuarios user1 INNER JOIN (SELECT ped.id, prod.nombre, ped.idUsuario, ped.cantidad, ped.diaPedido, ped.mesPedido, ped.diaEntrega, ped.mesEntrega FROM pedidos ped INNER JOIN productos prod ON ped.idProducto = prod.id) proPed ON user1.login = proPed.idUsuario WHERE user1.tipo = 'natural' OR user1.tipo = 'juridica')  WHERE " + condicionNombreCliente + " AND "+ condicionLoginCliente + " AND " + condicionPedido + " AND " + condicionProducto;
		System.out.println(sql);
		ResultSet rs = crud.darConexion().createStatement().executeQuery(sql);
		while(rs.next())
		{
			String login = rs.getString(1);		
			String nombre = rs.getString(2);
						
			String idPedido = rs.getString(3);
			String nombreProducto = rs.getString(4);
			int cantidad = Integer.parseInt(rs.getString(5));
			int diaPedido = Integer.parseInt(rs.getString(6));
			int mesPedido = Integer.parseInt(rs.getString(7));
			int diaEntrega = Integer.parseInt(rs.getString(8));
			int mesEntrega = Integer.parseInt(rs.getString(9));
			
			Date fechaPedido = new Date(2015, mesPedido, diaPedido);
			Date fechaEntrega = new Date(2015, mesEntrega, diaEntrega);
			Pedido pedido = new Pedido(idPedido, nombreProducto, login, nombre, cantidad, fechaPedido, fechaEntrega);
			System.out.println(pedido.getNombreCliente());
			rta.add(pedido);
		}
		return rta;
	}
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String darNombreProducto (String id) throws Exception{
		System.out.println("El id del producto es: " + id);
		return (crud.darSubTabla(Producto.NOMBRE, "nombre", "id = '" + id + "'")).get(0);
	}
	
	/**
	 * @param cantidad
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Producto> darCantidadProductos (int cantidad) throws Exception{
		ArrayList<Producto> rta = new ArrayList<Producto>();
		ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Producto.NOMBRE + "");
		while(rs.next())
		{
			String id = rs.getString(1);
			String nombre = rs.getString(2);
			int precio = rs.getInt(3);
			Producto producto = new Producto(id, nombre, precio);
			rta.add(producto);
		}
		return rta;
	}
	
	/**
	 * 
	 * @return
	 */
	public String darUsuarioActual(){
		return usuarioActual;
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Estacion> darEstaciones() throws Exception{
		ArrayList<Estacion> rta = new ArrayList<Estacion>();
		ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Estacion.NOMBRE + "");
		while(rs.next())
		{
			String id = rs.getString(1);
			String nombre = rs.getString(2);
			String tipo = rs.getString(3);
			Estacion estacion = new Estacion(id, nombre, tipo);
			rta.add(estacion);
		}
		return rta;
	}

	/**
	 * @param login
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Pedido> darPedidosCliente(String login) throws Exception{
		ArrayList<Pedido> rta = new ArrayList<Pedido>();
		Statement s = crud.darConexion().createStatement();
		String sql = "SELECT * FROM " + Pedido.NOMBRE + " WHERE idUsuario = '" + login + "'";
		System.out.println(sql);
		ResultSet rs = s.executeQuery(sql);
		while(rs.next()){
			String id = rs.getString(1);
			String idProducto = rs.getString(2);
			String idUsuario = rs.getString(3);
			int diaPedido = rs.getInt(4);
			int mesPedido = rs.getInt(5);
			int diaEntrega = rs.getInt(6);
			int mesEntrega = rs.getInt(7);
			int cantidad = rs.getInt(8);
			Date fechaPedido = new Date(2015, mesPedido, diaPedido);
			Date fechaEntrega = new Date(2015, mesEntrega, diaEntrega);
			Pedido pedido = new Pedido(id, idProducto, login, cantidad, fechaPedido, fechaEntrega);
			System.out.println(pedido.toString());
			rta.add(pedido);
		}
		return rta;
	}

	/**
	 * @param producto
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> darIdPedido (String producto) throws Exception{
		return crud.darSubTabla(Pedido.NOMBRE, "id", "idCliente = " + usuarioActual + "idProducto = " + producto);
	}
	
	public ArrayList<Producto> darProducto(String id) throws Exception{
		ArrayList<Producto> rta = new ArrayList<Producto>();
		String sql = "SELECT * FROM " + Producto.NOMBRE + " WHERE id = '" + id + "'";
		System.out.println(sql);
		ResultSet rs = crud.darConexion().createStatement().executeQuery(sql);
		while(rs.next())
		{
			String nombre = rs.getString(2);
			int precio = rs.getInt(3);
			Producto producto = new Producto(id, nombre, precio);
			rta.add(producto);
			System.out.println(producto.getNombre());
		}
		return rta;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<MateriaPrima> darMateriasPrimas( ) throws Exception {
		ArrayList<MateriaPrima> rta = new ArrayList<MateriaPrima>();
		ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + MateriaPrima.NOMBRE + "");
		while(rs.next())
		{
			String id = rs.getString(1);
			String unidadMedida = rs.getString(2);
			int cantidad = rs.getInt(3);
			MateriaPrima estacion = new MateriaPrima(id, unidadMedida, cantidad);
			rta.add(estacion);
		}
		return rta;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Componente> darComponentes( ) throws Exception {
		ArrayList<Componente> rta = new ArrayList<Componente>();
		ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Componente.NOMBRE + "");
		while(rs.next())
		{
			String id = rs.getString(1);
			int cantidad = rs.getInt(2);
			Componente estacion = new Componente(id, cantidad);
			rta.add(estacion);
		}
		return rta;
	}

	public ArrayList<MateriaPrima> darMateriasPrimasProveedor(String idProveedor) throws Exception{
		ArrayList<String> idsMateriasPrimas = crud.darSubTabla(Proveedor.NOMBRE_RELACION_MATERIA_PRIMA, "id_materiaPrima", "id_proveedor = '" + idProveedor + "'");
		ArrayList<MateriaPrima> rta = new ArrayList<MateriaPrima>();
		for (int i = 0; i < idsMateriasPrimas.size(); i++) {
			ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + MateriaPrima.NOMBRE + " WHERE id = '" + idsMateriasPrimas.get(i) + "'");
			while(rs.next())
			{
				String id = rs.getString(1);
				String unidadMedida = rs.getString(2);
				int cantidad = rs.getInt(3);
				MateriaPrima estacion = new MateriaPrima(id, unidadMedida, cantidad);
				rta.add(estacion);
			}
		}
		return rta;
	}
	
	public ArrayList<Producto> darProductosProveedor(String idProveedor) throws Exception{
		
		Set<Producto> setProductosProveedorMateriasPrimas = new HashSet<Producto>();
		
		ResultSet rsIdMateriasPrimas = crud.darConexion().createStatement().executeQuery("SELECT id_materiaPrima FROM " + Proveedor.NOMBRE_RELACION_MATERIA_PRIMA + " WHERE id_Proveedor = '" + idProveedor + "'");
		while(rsIdMateriasPrimas.next()){
			String idMateriaPrima = rsIdMateriasPrimas.getString(1);
			ResultSet rsIdRegistroMP = crud.darConexion().createStatement().executeQuery("SELECT id FROM " + MateriaPrima.NOMBRE_REGISTRO_MATERIAS_PRIMAS + " WHERE idMateriaPrima = '" + idMateriaPrima + "'");
			while(rsIdRegistroMP.next()){
				String idRegMateriaPrima = rsIdRegistroMP.getString(1);
				ResultSet rsIdInventarios = crud.darConexion().createStatement().executeQuery("SELECT idInventario FROM " + Producto.NOMBRE_REGISTRO_PRODUCTOS + " WHERE idRegistroMateriaPrima = '" + idRegMateriaPrima + "'");
				while(rsIdInventarios.next()){
					String idInventario = rsIdInventarios.getString(1);
					ResultSet rsIdProductos = crud.darConexion().createStatement().executeQuery("SELECT idProducto FROM " + Producto.NOMBRE_INVENTARIO_PRODUCTOS + " WHERE id = '" + idInventario + "'");
					while(rsIdProductos.next()){
						String idProducto = rsIdProductos.getString(1);
						ResultSet rsProductos = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Producto.NOMBRE + " WHERE id = '" + idProducto + "'");
						while(rsProductos.next())
						{
							String id = rsProductos.getString(1);
							String nombre = rsProductos.getString(2);
							int precio = rsProductos.getInt(3);
							Producto producto = new Producto(id, nombre, precio);
							setProductosProveedorMateriasPrimas.add(producto);
						}
					}
				}
			}	
		}
		
		Set<Producto> setProductosProveedorComponentes = new HashSet<Producto>();
		
		ResultSet rsIdComponentes = crud.darConexion().createStatement().executeQuery("SELECT id_Componente FROM " + Proveedor.NOMBRE_RELACION_COMPONENTE + " WHERE id_Proveedor = '" + idProveedor + "'");
		while(rsIdComponentes.next()){
			String idComponente = rsIdComponentes.getString(1);
			ResultSet rsIdInventarios = crud.darConexion().createStatement().executeQuery("SELECT idInventario FROM " + Producto.NOMBRE_REGISTRO_PRODUCTOS + " WHERE idComponente = '" + idComponente + "'");
			while(rsIdInventarios.next()){
				String idInventario = rsIdInventarios.getString(1);
				ResultSet rsIdProductos = crud.darConexion().createStatement().executeQuery("SELECT idProducto FROM " + Producto.NOMBRE_INVENTARIO_PRODUCTOS + " WHERE id = '" + idInventario + "'");
				while(rsIdProductos.next()){
					String idProducto = rsIdProductos.getString(1);
					ResultSet rsProductos = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Producto.NOMBRE + " WHERE id = '" + idProducto + "'");
					while(rsProductos.next())
					{
						String id = rsProductos.getString(1);
						String nombre = rsProductos.getString(2);
						int precio = rsProductos.getInt(3);
						Producto producto = new Producto(id, nombre, precio);
						setProductosProveedorComponentes.add(producto);
					}
				}
			}

		}
		ArrayList<Producto> productosProveedor = new ArrayList<Producto>();
		productosProveedor.addAll(setProductosProveedorMateriasPrimas);
		productosProveedor.addAll(setProductosProveedorComponentes);
		return productosProveedor;
	}
	
	public ArrayList<Pedido> darPedidosProveedor(String idProveedor){
		
		try{
			ArrayList<Producto> productosProveedor = darProductosProveedor(idProveedor);
			
			Set<Pedido> setPedidosProveedor = new HashSet<Pedido>();
			for(int i = 0; i < productosProveedor.size(); i++){
				Producto productoActual = productosProveedor.get(i);
				String idProductoActual = productoActual.getId();
				ResultSet rsPedidos = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Pedido.NOMBRE + " WHERE idProducto = '" + idProductoActual + "'");
				while(rsPedidos.next()){
					String id = rsPedidos.getString(1);
					String idProducto = rsPedidos.getString(2);
					String idUsuario = rsPedidos.getString(3);
					int diaPedido = rsPedidos.getInt(4);
					int mesPedido = rsPedidos.getInt(5);
					int diaEntrega = rsPedidos.getInt(6);
					int mesEntrega = rsPedidos.getInt(7);
					int cantidad = rsPedidos.getInt(8);
					Date fechaPedido = new Date(2015, mesPedido, diaPedido);
					Date fechaEntrega = new Date(2015, mesEntrega, diaEntrega);
					Pedido pedido = new Pedido(id, idProducto, idUsuario, cantidad, fechaPedido, fechaEntrega);	
					setPedidosProveedor.add(pedido);
				}
			}
			ArrayList<Pedido> pedidosProveedor = new ArrayList<Pedido>();
			pedidosProveedor.addAll(setPedidosProveedor);
			pedidosProveedor.addAll(setPedidosProveedor);
			return pedidosProveedor;	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * @param idProveedor
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Componente> darComponentesProveedor(String idProveedor) throws Exception {
		ArrayList<String> idsComponentes = crud.darSubTabla(Proveedor.NOMBRE_RELACION_COMPONENTE, "id_componente", "id_proveedor = '" + idProveedor + "'");
		ArrayList<Componente> rta = new ArrayList<Componente>();
		for (int i = 0; i < idsComponentes.size(); i++) {
			ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Componente.NOMBRE + " WHERE id = '" + idsComponentes.get(i) + "'");
			while(rs.next())
			{
				String id = rs.getString(1);
				int cantidad = rs.getInt(2);
				Componente estacion = new Componente(id, cantidad);
				rta.add(estacion);
			}
		}
		return rta;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Usuario> darClientes(String condicionNombre, String condicionPedido, String condicionProducto) throws Exception{
		ArrayList<Usuario> rta = new ArrayList<Usuario>();
		String sql = "SELECT * FROM (SELECT user1.*, proPed.id AS idPedido, proPed.nombre AS nombreProducto, proPed.cantidad AS cantidad, proPed.diaPedido AS diaPedido, proPed.mesPedido AS mesPedido, proPed.diaEntrega AS diaEntrega, proPed.mesEntrega AS mesEntrega FROM usuarios user1 LEFT JOIN (SELECT ped.id, prod.nombre, ped.idUsuario, ped.cantidad, ped.diaPedido, ped.mesPedido, ped.diaEntrega, ped.mesEntrega FROM pedidos ped INNER JOIN productos prod ON ped.idProducto = prod.id) proPed ON user1.login = proPed.idUsuario WHERE user1.tipo = 'natural' OR user1.tipo = 'juridica')  WHERE " + condicionNombre + " AND " + condicionPedido + " AND " + condicionProducto;
		System.out.println(sql);
		ResultSet rs = crud.darConexion().createStatement().executeQuery(sql);
		while(rs.next())
		{
			String login = rs.getString(1);
			
			System.out.println(login);
			
			String tipo = rs.getString(3);
			String nombre = rs.getString(4);
			String direccion = rs.getString(5);
			int telefono = 0;
			if (rs.getString(6) != null)
				telefono = Integer.parseInt(rs.getString(6));
			String ciudad = rs.getString(7);
			String idRepLegal = rs.getString(8);
			
			Usuario user = new Usuario(login, tipo, "", nombre, direccion, telefono, ciudad, idRepLegal);
						
			String idPedido = rs.getString(9);
			String nombreProducto = rs.getString(10);
			int cantidad = 0;
			if (rs.getString(11) != null)
				cantidad = Integer.parseInt(rs.getString(11));
			int diaPedido = 0;
			if (rs.getString(12) != null)
				diaPedido = Integer.parseInt(rs.getString(12));
			int mesPedido = 0;
			if (rs.getString(13) != null)
				mesPedido = Integer.parseInt(rs.getString(13));
			int diaEntrega = 0;
			if (rs.getString(14) != null)
				diaEntrega = Integer.parseInt(rs.getString(14));
			int mesEntrega = 0;
			if (rs.getString(15) != null)
				mesEntrega = Integer.parseInt(rs.getString(15));
			
			Date fechaPedido = new Date(2015, mesPedido, diaPedido);
			Date fechaEntrega = new Date(2015, mesEntrega, diaEntrega);
			Pedido pedido = null;
			boolean encontro = false;
			if (idPedido != null)
			{
				pedido = new Pedido(idPedido, nombreProducto, login, cantidad, fechaPedido, fechaEntrega);
				
				user.addPedido(pedido);
				for (int i = 0; i < rta.size() && !encontro; i++) {
					if (rta.get(i).getLogin().equals(login))
					{
						user.addAllPedidos(rta.get(i).getPedidos());
						rta.remove(rta.get(i));
						rta.add(user);
						encontro = true;
					}	
				}
			}
		
			if(!encontro)
				rta.add(user);
		}
		return rta;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Proveedor> darProveedores(String condicionProveedores, String condicionMateriasPrimas, String condicionComponentes) throws Exception {
		ArrayList<Proveedor> rta = new ArrayList<Proveedor>();
		ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT * FROM " + Proveedor.NOMBRE + " WHERE " + condicionProveedores );
		while(rs.next())
		{
			String pId = rs.getString(1);
			String pDireccion = rs.getString(2);
			int pTelefono = Integer.parseInt(rs.getString(3));
			String pCiudad = rs.getString(4);
			String pIdRepLegal = rs.getString(5);
			
			Proveedor prov = new Proveedor(pId, pDireccion, pTelefono, pCiudad, pIdRepLegal);
			
			ArrayList<String> idMateriaPrima = new ArrayList<String>();
			ArrayList<String> idComponente = new ArrayList<String>();
			
			String sql_materias = "SELECT * FROM " + Proveedor.NOMBRE_RELACION_MATERIA_PRIMA + " WHERE id_proveedor = '" + prov.getId() + "' AND " + condicionMateriasPrimas;
			ResultSet rs_materias = crud.darConexion().createStatement().executeQuery(sql_materias);
			while(rs_materias.next())
			{
				idMateriaPrima.add(rs_materias.getString(2));
			}
			String sql_componentes = "SELECT * FROM " + Proveedor.NOMBRE_RELACION_COMPONENTE + " WHERE id_proveedor = '" + prov.getId() + "' AND " + condicionComponentes;
			ResultSet rs_componentes = crud.darConexion().createStatement().executeQuery(sql_componentes);
			while(rs_componentes.next())
			{
				idComponente.add(rs_componentes.getString(2));
			}
			for (String materiaPrima : idMateriaPrima) {
				String sql_materiasPrimas = "SELECT * FROM " + MateriaPrima.NOMBRE + " WHERE id = '" + materiaPrima + "'";
				ResultSet rs_1 = crud.darConexion().createStatement().executeQuery(sql_materiasPrimas);
				while(rs_1.next())
				{
					String id = rs_1.getString(1);
					String unidadMedida = rs_1.getString(2);
					int cantidadInicial = rs_1.getInt(3);
					MateriaPrima m = new MateriaPrima(id, unidadMedida, cantidadInicial);
					prov.addMateriaPrima(m);
				}
			}
			for (String componente : idComponente) {
				String sql_componentesProv = "SELECT * FROM " + Componente.NOMBRE + " WHERE id = '" + componente + "'";
				ResultSet rs_1 = crud.darConexion().createStatement().executeQuery(sql_componentesProv);
				while(rs_1.next())
				{
					String id = rs_1.getString(1);
					int cantidadInicial = rs_1.getInt(2);
					Componente c = new Componente(id, cantidadInicial);
					prov.addComponente(c);
				}
			}
			if (prov.getComponentes().size() != 0 || prov.getMateriasPrimas().size() != 0)
				rta.add(prov);
		}
		return rta;
	}
	
	//--------------------------------------------------
	// METODOS ELIMINAR
	//--------------------------------------------------
	
	/**
	 * @param login
	 * @param idPedido
	 * @throws Exception
	 */
	public void eliminarPedidoCliente(String login, String idPedido) throws Exception{
		conexion.setAutoCommitFalso();
		Savepoint save = conexion.darConexion().setSavepoint();
		try
		{
			ResultSet rs = crud.darConexion().createStatement().executeQuery("SELECT id FROM " + Producto.NOMBRE_INVENTARIO_PRODUCTOS + " WHERE " + "idPedido = '" + idPedido + "'");
			while(rs.next()){
				crud.eliminarTupla(Producto.NOMBRE_REGISTRO_PRODUCTOS, "idInventario = '" + rs.getString(1) + "'");
			}
			crud.eliminarTupla(Producto.NOMBRE_INVENTARIO_PRODUCTOS, "idPedido = '" + idPedido + "'");
			crud.eliminarTuplaPorId(Pedido.NOMBRE, idPedido);
		}
		catch(Exception e){
			conexion.darConexion().rollback(save);
			e.printStackTrace();
		}
		conexion.darConexion().commit();
		conexion.setAutoCommitVerdadero();
	}
	
	public void desactivarEstacionProduccion(String idEstacion){
		try {
		conexion.setAutoCommitFalso();
		Savepoint save = conexion.darConexion().setSavepoint();
		} 
		catch (SQLException e) {
		e.printStackTrace();
		}
		}
	
	//--------------------------------------------------
	// MAIN
	//--------------------------------------------------

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AplicacionWeb aplicacionWeb = getInstancia();
		try
		{
			Date dia = new Date();
			aplicacionWeb.registrarPedido("Mangou", "2", 1, dia);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
