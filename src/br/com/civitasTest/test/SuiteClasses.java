package br.com.civitasTest.test;

import java.io.File;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	
	//TESTES COMUNS AOS SISTEMAS
	TestAlteraSenha.class
})

public class SuiteClasses {
	//TODO: ajustar PATH suite class
	public static final String PATH_DATASET = System.getProperty("user.dir") + "/src/test/resources/dataSet/".replace("/", File.separator);
}
