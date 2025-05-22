import datetime
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib.units import inch, cm
from reportlab.lib.pagesizes import A4
from reportlab.lib.colors import navy, black, gray, lightgrey

# Attempt to import models for type hinting (optional, can use 'Any' or forward refs)
try:
    from . import models
    _models_available = True
except ImportError:
    _models_available = False
    # Define dummy classes if models.py is not available, for type hinting or standalone use
    class DummyUser:
        def __init__(self, username="<username>"): self.username = username
    class DummyRole:
        def __init__(self, nombre_role="<role_name>"): self.nombre_role = nombre_role
    class DummyCanalEntrada:
        def __init__(self, nombre="<canal>"): self.nombre = nombre
    class DummyCategoria:
        def __init__(self, nombre="<categoria>"): self.nombre = nombre
    class DummyComentario:
        def __init__(self, usuario=None, posicion_usuario="<posicion>", fecha=None, hora=None, texto_comentario="<texto>", visto=False):
            self.usuario = usuario or DummyUser()
            self.posicion_usuario = posicion_usuario
            self.fecha = fecha or datetime.date.today()
            self.hora = hora or datetime.time(12,0,0)
            self.texto_comentario = texto_comentario
            self.visto = visto
    class DummyEntrada: # Ensure this matches models.Entrada structure closely for attribute access
        def __init__(self, id=0, asunto="<asunto>", fecha=None, numero_entrada="<num>", area=None, canal_entrada=None,
                     confidencial=False, urgente=False, tramitado=False, tramitado_por=None, observaciones="<obs>",
                     destinatarios=None, categorias=None, comentarios=None, 
                     archivos=None, antecedentes=None, salidas=None): # Added file lists
            self.id = id; self.asunto = asunto; self.fecha = fecha or datetime.date.today()
            self.numero_entrada = numero_entrada; self.area = area or DummyRole()
            self.canal_entrada = canal_entrada or DummyCanalEntrada(); self.confidencial = confidencial
            self.urgente = urgente; self.tramitado = tramitado; self.tramitado_por = tramitado_por or DummyUser()
            self.observaciones = observaciones; self.destinatarios = destinatarios or []
            self.categorias = categorias or []; self.comentarios = comentarios or []
            self.archivos = archivos or [] # Initialize file lists
            self.antecedentes = antecedentes or []
            self.salidas = salidas or []


    models_content = {
        'Entrada': DummyEntrada, 'Usuario': DummyUser, 'Role': DummyRole, 
        'CanalEntrada': DummyCanalEntrada, 'Categoria': DummyCategoria, 'Comentario': DummyComentario
    }
    # Check if 'models' is already a module (from a previous failed import but partial success)
    if not _models_available or not hasattr(models, 'Entrada'):
        models = type('models_dummy', (object,), models_content)


def generate_entrada_report(entrada_obj: models.Entrada, output_path: str) -> bool:
    """
    Generates a PDF report for a given Entrada object.
    """
    try:
        doc = SimpleDocTemplate(output_path, pagesize=A4,
                                rightMargin=0.75*inch, leftMargin=0.75*inch,
                                topMargin=0.75*inch, bottomMargin=0.75*inch)
        styles = getSampleStyleSheet()
        story = []

        # Title
        story.append(Paragraph("Informe de Entrada", styles['h1']))
        story.append(Spacer(1, 0.2*inch))
        story.append(Paragraph(f"Asunto: {entrada_obj.asunto or 'N/A'}", styles['h2']))
        story.append(Spacer(1, 0.3*inch))

        # Main Details Table
        details_data = [
            ["ID Entrada:", str(entrada_obj.id)],
            ["Fecha:", entrada_obj.fecha.strftime("%d/%m/%Y") if entrada_obj.fecha else "N/A"],
            ["Número Entrada:", entrada_obj.numero_entrada or "N/A"],
            ["Área Principal:", entrada_obj.area.nombre_role if entrada_obj.area and hasattr(entrada_obj.area, 'nombre_role') else "N/A"],
            ["Canal de Entrada:", entrada_obj.canal_entrada.nombre if entrada_obj.canal_entrada and hasattr(entrada_obj.canal_entrada, 'nombre') else "N/A"],
            ["Confidencial:", "Sí" if entrada_obj.confidencial else "No"],
            ["Urgente:", "Sí" if entrada_obj.urgente else "No"],
            ["Tramitado:", "Sí" if entrada_obj.tramitado else "No"],
            ["Tramitado Por:", entrada_obj.tramitado_por.username if entrada_obj.tramitado_por and hasattr(entrada_obj.tramitado_por, 'username') else "N/A"],
        ]
        
        table_style = TableStyle([
            ('GRID', (0,0), (-1,-1), 0.5, colors.grey),
            ('BACKGROUND', (0,0), (0,-1), colors.lightgrey), 
            ('ALIGN', (0,0), (-1,-1), 'LEFT'),
            ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
            ('FONTNAME', (0,0), (0,-1), 'Helvetica-Bold'), 
            ('FONTNAME', (1,0), (1,-1), 'Helvetica'),      
            ('LEFTPADDING', (0,0), (-1,-1), 6),
            ('RIGHTPADDING', (0,0), (-1,-1), 6),
            ('TOPPADDING', (0,0), (-1,-1), 4),
            ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ])
        details_table = Table(details_data, colWidths=[2*inch, 4.5*inch])
        details_table.setStyle(table_style)
        story.append(details_table)
        story.append(Spacer(1, 0.1*inch)) # Reduced spacer

        # Observaciones (multi-line)
        story.append(Paragraph("<b>Observaciones:</b>", styles['Normal']))
        obs_text = (entrada_obj.observaciones or "Sin observaciones.").replace('\n', '<br/>\n')
        story.append(Paragraph(obs_text, styles['Normal']))
        story.append(Spacer(1, 0.3*inch))

        # Destinatarios
        if entrada_obj.destinatarios:
            story.append(Paragraph("Destinatarios:", styles['h3']))
            dest_text = ", ".join([getattr(d, 'nombre_role', 'N/A') for d in entrada_obj.destinatarios])
            story.append(Paragraph(dest_text, styles['Normal']))
            story.append(Spacer(1, 0.2*inch))

        # Categorías
        if entrada_obj.categorias:
            story.append(Paragraph("Categorías:", styles['h3']))
            cat_text = ", ".join([getattr(c, 'nombre', 'N/A') for c in entrada_obj.categorias])
            story.append(Paragraph(cat_text, styles['Normal']))
            story.append(Spacer(1, 0.3*inch))

        # Comentarios
        if entrada_obj.comentarios:
            story.append(Paragraph("Comentarios de Jefes:", styles['h3']))
            for comentario in entrada_obj.comentarios:
                commenter_name = "N/A"
                if hasattr(comentario, 'usuario') and comentario.usuario and hasattr(comentario.usuario, 'username'):
                    commenter_name = comentario.usuario.username
                elif hasattr(comentario, 'posicion_usuario') and comentario.posicion_usuario: 
                    commenter_name = comentario.posicion_usuario
                
                fecha_str = comentario.fecha.strftime("%d/%m/%Y") if comentario.fecha else "N/A"
                hora_str = comentario.hora.strftime("%H:%M:%S") if isinstance(comentario.hora, datetime.time) else str(comentario.hora or "N/A")
                visto_str = "Sí" if comentario.visto else "No"
                
                comment_header = f"<b>De:</b> {commenter_name} - <b>Fecha:</b> {fecha_str} {hora_str} - <b>Visto:</b> {visto_str}"
                story.append(Paragraph(comment_header, styles['Normal']))
                
                comment_text = (comentario.texto_comentario or "Sin texto.").replace('\n', '<br/>\n')
                story.append(Paragraph(comment_text, styles['Normal']))
                story.append(Spacer(1, 0.2*inch))

        doc.build(story)
        return True
    except Exception as e:
        print(f"Error generating PDF report: {e}")
        return False

# --- Standalone Test ---
if __name__ == '__main__':
    import os # For os.path.abspath
    print("Running report_generator.py standalone test...")

    # Create comprehensive mock objects
    mock_user_tramitador = models.Usuario(username="operador_x")
    mock_user_jefe1 = models.Usuario(username="jefe_area_1", id=101)
    mock_user_jefe2 = models.Usuario(username="jefe_seccion_b", id=102)

    mock_area_principal = models.Role(nombre_role="Coordinación General")
    mock_destinatario1 = models.Role(nombre_role="Departamento Legal")
    mock_destinatario2 = models.Role(nombre_role="Jefatura de Operaciones (JOp)") 

    mock_canal = models.CanalEntrada(nombre="Correo Electrónico Oficial")
    
    mock_categoria1 = models.Categoria(nombre="Prioritario")
    mock_categoria2 = models.Categoria(nombre="Revisión Técnica")

    mock_comentario1 = models.Comentario(
        usuario=mock_user_jefe1,
        posicion_usuario="Jefe Área Legal", 
        fecha=datetime.date(2023, 11, 15),
        hora=datetime.time(10, 30, 0),
        texto_comentario="Revisado. Proceder con la fase de análisis legal.",
        visto=True
    )
    mock_comentario2 = models.Comentario(
        usuario=mock_user_jefe2,
        posicion_usuario="Jefe Sección Técnica B",
        fecha=datetime.date(2023, 11, 16),
        hora=datetime.time(9, 15, 0),
        texto_comentario="De acuerdo con el comentario anterior.\nSe requiere informe técnico detallado para el día 20.", # Added newline
        visto=False
    )

    mock_entrada = models.Entrada(
        id=789,
        asunto="Solicitud Urgente de Revisión de Contrato Marco XYZ/2023",
        fecha=datetime.date(2023, 11, 14),
        numero_entrada="E-2023-11-0789",
        area=mock_area_principal,
        canal_entrada=mock_canal,
        confidencial=True,
        urgente=True,
        tramitado=False,
        tramitado_por=None, 
        observaciones="Se adjuntan documentos preliminares.\nSe requiere máxima celeridad debido a plazos contractuales.\nFavor de enfocar en cláusulas 3, 7 y Anexo B.\nNotificar avances a todos los destinatarios.",
        destinatarios=[mock_destinatario1, mock_destinatario2],
        categorias=[mock_categoria1, mock_categoria2],
        comentarios=[mock_comentario1, mock_comentario2]
    )

    output_pdf_path = "informe_entrada_test.pdf"
    if generate_entrada_report(mock_entrada, output_pdf_path):
        print(f"PDF de prueba generado exitosamente: {os.path.abspath(output_pdf_path)}")
    else:
        print(f"Fallo al generar el PDF de prueba.")

```
