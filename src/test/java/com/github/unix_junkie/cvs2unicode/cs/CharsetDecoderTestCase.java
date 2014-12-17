/*-
 * $Id$
 */
package com.github.unix_junkie.cvs2unicode.cs;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.unix_junkie.cvs2unicode.CharsetDecoder;

/**
 * @author Andrew ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
@RunWith(JUnit4.class)
public final class CharsetDecoderTestCase {
	@SuppressWarnings("static-method")
	@Test
	public void testUku() throws CharacterCodingException {
		final CharsetDecoder uku = new UKU();
		final byte utf8Data[] = {
				(byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x96, (byte) 0x91, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x96, (byte) 0x92, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x96, (byte) 0x93, (byte) 0xd0,
				(byte) 0xbf, (byte) 0xe2, (byte) 0x8c, (byte) 0xa0, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x96, (byte) 0xa0, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x88, (byte) 0x99, (byte) 0xd0, (byte) 0xbf,
				(byte) 0xe2, (byte) 0x94, (byte) 0x82, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x88, (byte) 0x9a, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x89, (byte) 0x88, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2,
				(byte) 0x89, (byte) 0xa4, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x89, (byte) 0xa5, (byte) 0xd0, (byte) 0xbf, (byte) 0xc2, (byte) 0xa0, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x8c, (byte) 0xa1,
				(byte) 0xd0, (byte) 0xbf, (byte) 0xc2, (byte) 0xb0, (byte) 0xd0, (byte) 0xbf, (byte) 0xc2, (byte) 0xb2, (byte) 0xd0, (byte) 0xbf, (byte) 0xc2, (byte) 0xb7, (byte) 0xd0, (byte) 0xbf, (byte) 0xc3, (byte) 0xb7,
				(byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x90, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x91, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x92, (byte) 0xd0,
				(byte) 0xbf, (byte) 0xd1, (byte) 0x91, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x93, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x94, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2,
				(byte) 0x95, (byte) 0x95, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x96, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x97, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95,
				(byte) 0x98, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x99, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x9a, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x9b,
				(byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x9c, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x9d, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x9e, (byte) 0xd0,
				(byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0x9f, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa0, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa1, (byte) 0xd0, (byte) 0xbf,
				(byte) 0xd0, (byte) 0x81, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa2, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa3, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x96,
				(byte) 0x92, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa4, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa5, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa6,
				(byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa7, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa8, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xa9, (byte) 0xd0,
				(byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xaa, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xab, (byte) 0xd0, (byte) 0xbf, (byte) 0xe2, (byte) 0x95, (byte) 0xac, (byte) 0xd0, (byte) 0xbf,
				(byte) 0xc2, (byte) 0xa9, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0x80, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0x82, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94,
				(byte) 0x8c, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0x90, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0x94, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0x98,
				(byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0x9c, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0xa4, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0xac, (byte) 0xd1,
				(byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0xb4, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x94, (byte) 0xbc, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x96, (byte) 0x80, (byte) 0xd1, (byte) 0x8f,
				(byte) 0xe2, (byte) 0x96, (byte) 0x84, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x96, (byte) 0x88, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2, (byte) 0x96, (byte) 0x8c, (byte) 0xd1, (byte) 0x8f, (byte) 0xe2,
				(byte) 0x96, (byte) 0x90,
		};
		assertEquals("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя", uku.decode(utf8Data));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testUkuSymmetry() throws CharacterCodingException {
		final String s = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
		final CharsetDecoder uku = new UKU();
		final ByteBuffer data = uku.encode(s);
		assertEquals(s, uku.decode(data));
		assertEquals(data, uku.encode(s).position(0));
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @throws CharacterCodingException
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testDosAsUnicode() throws UnsupportedEncodingException, CharacterCodingException {
		final CharsetDecoder dosAsUnicode = new DosAsUnicode();

		final byte cyrillicAlphabetInCp866[] = {
				(byte) 0x80, (byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0xf0, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x8b, (byte) 0x8c, (byte) 0x8d, (byte) 0x8e,
				(byte) 0x8f, (byte) 0x90, (byte) 0x91, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9a, (byte) 0x9b, (byte) 0x9c, (byte) 0x9d, (byte) 0x9e,
				(byte) 0x9f, (byte) 0xa0, (byte) 0xa1, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5, (byte) 0xf1, (byte) 0xa6, (byte) 0xa7, (byte) 0xa8, (byte) 0xa9, (byte) 0xaa, (byte) 0xab, (byte) 0xac, (byte) 0xad,
				(byte) 0xae, (byte) 0xaf, (byte) 0xe0, (byte) 0xe1, (byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7, (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xeb, (byte) 0xec, (byte) 0xed,
				(byte) 0xee, (byte) 0xef,
		};

		assertEquals("���������������������������������������ёжзи�����������������������", dosAsUnicode.decode(cyrillicAlphabetInCp866));
		assertEquals("����", dosAsUnicode.decode("Цвет".getBytes("IBM866")));
		assertEquals("���щин� �����", dosAsUnicode.decode("Толщина линии".getBytes("IBM866")));
		assertEquals("�тил� �����", dosAsUnicode.decode("Стиль линии".getBytes("IBM866")));
		assertEquals("���соб укл���� ������", dosAsUnicode.decode("Способ укладки кабеля".getBytes("IBM866")));
		assertEquals("��уби�� ���������", dosAsUnicode.decode("Глубина заложения".getBytes("IBM866")));
		assertEquals("������� ����", dosAsUnicode.decode("Диаметр трубы".getBytes("IBM866")));
		assertEquals("���риа�", dosAsUnicode.decode("Материал".getBytes("IBM866")));
		assertEquals("���соб укл���� ����", dosAsUnicode.decode("Способ укладки трубы".getBytes("IBM866")));
		assertEquals("���ужи����� ������", dosAsUnicode.decode("Обслуживание колодца".getBytes("IBM866")));
		assertEquals("��щее ���яни� ������", dosAsUnicode.decode("Общее состояние колодца".getBytes("IBM866")));
		assertEquals("������� ����� ���", dosAsUnicode.decode("Емкость блока труб".getBytes("IBM866")));
		assertEquals("��сло ������� � �сно����� �����", dosAsUnicode.decode("Число каналов в основания блока".getBytes("IBM866")));
	}

	@SuppressWarnings("static-method")
	@Test
	public void testDosAsUnicodeSymmetry() throws CharacterCodingException {
		final CharsetDecoder dosAsUnicode = new DosAsUnicode();

		final ByteBuffer buf = dosAsUnicode.encode("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя");
		final byte data[] = new byte[3];
		buf.get(data, 0, 3);
		buf.position(0);
		assertEquals('�', new UTF_8().decode(data).charAt(0));

		assertEquals(buf.limit(), buf.capacity());

		assertEquals("���������������������������������������ёжзи�����������������������", dosAsUnicode.decode(buf));
		assertEquals("����", dosAsUnicode.decode(dosAsUnicode.encode("Цвет")));
		assertEquals("���щин� �����", dosAsUnicode.decode(dosAsUnicode.encode("Толщина линии")));
		assertEquals("�тил� �����", dosAsUnicode.decode(dosAsUnicode.encode("Стиль линии")));
		assertEquals("���соб укл���� ������", dosAsUnicode.decode(dosAsUnicode.encode("Способ укладки кабеля")));
		assertEquals("��уби�� ���������", dosAsUnicode.decode(dosAsUnicode.encode("Глубина заложения")));
		assertEquals("������� ����", dosAsUnicode.decode(dosAsUnicode.encode("Диаметр трубы")));
		assertEquals("���риа�", dosAsUnicode.decode(dosAsUnicode.encode("Материал")));
		assertEquals("���соб укл���� ����", dosAsUnicode.decode(dosAsUnicode.encode("Способ укладки трубы")));
		assertEquals("���ужи����� ������", dosAsUnicode.decode(dosAsUnicode.encode("Обслуживание колодца")));
		assertEquals("��щее ���яни� ������", dosAsUnicode.decode(dosAsUnicode.encode("Общее состояние колодца")));
		assertEquals("������� ����� ���", dosAsUnicode.decode(dosAsUnicode.encode("Емкость блока труб")));
		assertEquals("��сло ������� � �сно����� �����", dosAsUnicode.decode(dosAsUnicode.encode("Число каналов в основания блока")));
	}
}
