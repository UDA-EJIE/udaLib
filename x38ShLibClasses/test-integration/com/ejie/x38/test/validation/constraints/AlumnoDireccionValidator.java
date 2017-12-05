package com.ejie.x38.test.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ejie.x38.test.bean.Alumno;

public class AlumnoDireccionValidator implements ConstraintValidator<AlumnoDireccion, Alumno> {

	@Override
	public void initialize(AlumnoDireccion constraintAnnotation) {
	}

	@Override
	public boolean isValid(Alumno alumno, ConstraintValidatorContext constraintContext) {
		
		boolean isValid=true;
		
		constraintContext.disableDefaultConstraintViolation();
		if (alumno!=null){
			if (alumno.getPais()!=null && "108".equals(alumno.getPais().getId())){
				// Se ha seleccionado el pais 'Espanya'
				if (alumno.getAutonomia()==null || alumno.getAutonomia().getId()== null || "".equals(alumno.getAutonomia().getId())){
					isValid=false;
					constraintContext.buildConstraintViolationWithTemplate("validacion.required").addNode("autonomia.id").addConstraintViolation();
				}
				
				if (alumno.getProvincia()==null || alumno.getProvincia().getId()== null || "".equals(alumno.getProvincia().getId())){
					isValid=false;
					constraintContext.buildConstraintViolationWithTemplate("validacion.required").addNode("provincia.id").addConstraintViolation();
				}
				
			}else{
				if (alumno.getDireccion()==null || "".equals(alumno.getDireccion())){
					isValid=false;
					constraintContext.buildConstraintViolationWithTemplate("validacion.required").addNode("direccion").addConstraintViolation();
				}
			}
		}
		
		// TODO Auto-generated method stub
		return isValid;
	}
	
	

}
