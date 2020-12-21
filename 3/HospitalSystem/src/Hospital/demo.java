package Hospital;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


import java.time.*;

class Person {// reservation patient
   String name;
   String id_num;
   
   public Person(String name, String id_num) {
      this.name = name;
      this.id_num = id_num;
   }

   public boolean requestToEMR() throws IOException {// to outer system
      if (EMR.checkPersonalInfo(this) == true)
         return true;
      else
         return false;
   }

   public String toString() {
      return name;
   }

}


class newPerson extends Person {

   String addr;

   public newPerson(String name, String id_num, String addr) throws IOException {
      super(name, id_num);
      this.addr = addr;
      EMR.save(this);
   }
}





class GetCurrentTime_p {
   //Date, Time
   //fixme
}

class Inner_Pay{
	Person p;
	
	public void setPersonInfo(Person p) {
		this.p = p;
	}
}



class EMR {// outer
   public static boolean checkPersonalInfo(Person p) throws IOException {
      // file(patient db) io
      String[] ParsedOneline = null;
      BufferedReader br = new BufferedReader(new FileReader("PatientDB.txt"));
      while (true) {
         String line = br.readLine();

         if (line == null)
            break;
         ParsedOneline = line.split(" ");
         if (ParsedOneline[0].equals(p.id_num) && ParsedOneline[1].equals(p.name)) {
            return true;
         }
      }

      br.close();
      return false;

   }
   
   public static PatientInformation getPatientInformation(PatientInformation pi) {
	   return new PatientInformation();
   }

   public static boolean save(newPerson p) throws IOException {
      // file (patient db) io
      FileOutputStream output = new FileOutputStream("PatientDB.txt", true);
      String data = "\n" + p.id_num + " " + p.name + " " + p.addr;
      output.write(data.getBytes());
      System.out.println("신규 등록 완료\n");
      output.close();
      BufferedReader br = new BufferedReader(new FileReader("PatientDB.txt"));
      while (true) {
         String line = br.readLine();

         if (line == null)
            break;
      }

      br.close();
      return true;
   }

   public static Prescription showPrescription(Person p) throws IOException {
      // file io
      Prescription pre = new Prescription();
      String[] ParsedOneline = null;
      BufferedReader br = new BufferedReader(new FileReader("Prescription.txt"));
      while (true) {
         String line = br.readLine();

         if (line == null)
            break;
         ParsedOneline = line.split(" ");

         if (ParsedOneline[0].equals(p.id_num)) {
            pre.medicine = ParsedOneline[2] + " " + ParsedOneline[3];
         }
      }

      br.close();

      return pre;

   }

   public static int showTotalPrice(Person p) throws IOException {
      // file io
      int sum = 100;
      String[] ParsedOneline = null;
      BufferedReader br = new BufferedReader(new FileReader("Payment.txt"));
      while (true) {
         String line = br.readLine();

         if (line == null)
            break;
         ParsedOneline = line.split(" ");

         if (ParsedOneline[0].equals(p.id_num)) {
            String temp = ParsedOneline[2];
            sum = Integer.parseInt(temp);
         }
      }

      br.close();

      return sum;
   }

}

class Doctor {// outer, db

   private static ArrayList<String> doctor = new ArrayList<String>();

   public static void showDoctorInfo() throws IOException {
      BufferedReader br = new BufferedReader(new FileReader("DoctorDB.txt"));
      while (true) {
         String line = br.readLine();

         if (line == null)
            break;

         doctor.add(line);
      }

      br.close();

      for (String str : doctor) {
         System.out.println(str);
      }
   }

   public static boolean IsValidDoctor(String name) {
      String[] ParsedOneline = null;

      for (String str : doctor) {
         ParsedOneline = str.split(" ");
         if (name.equals(ParsedOneline[0]))
            return true;
      }
      return false;
   }
}

class ReservationInfo {// outer
   String reservation_num;
   String d_name;
   Person p;

   private ReservationInfo(String num, Person p, String d_name) {
      this.reservation_num = num;
      this.p = p;
      this.d_name = d_name;
   }

   static public ReservationInfo checkReservationInfo(String num) throws IOException {
      // file io
      // if(num is true)
      String[] ParsedOneline = null;
      BufferedReader br = new BufferedReader(new FileReader("ReservationDB.txt"));
      while (true) {
         String line = br.readLine();

         if (line == null)
            break;
         ParsedOneline = line.split(" ");

         if (ParsedOneline[0].equals(num)) {
            //return new ReservationInfo(ParsedOneline[0], ParsedOneline[1], ParsedOneline[2], ParsedOneline[3]);// fix me
         }
      }

      br.close();

      return null;

   }
}

class CheckReservation {
   private static ReservationInfo r_info;

   public static ReservationInfo checkReservationNum(String num) throws IOException {
      r_info = ReservationInfo.checkReservationInfo(num);
      if (r_info != null) {

         return r_info;
      } else
         return null;
   }

}

class WaitingQueueList {// 대기줄 관리
   static ArrayList<WaitingQueue> Qlist = new ArrayList<WaitingQueue>();

   static public void pushToQueueList(WaitingQueue q) {
      Qlist.add(q);
   }

   static public void deleteToQueue(String patient_name) {
      for (WaitingQueue q : Qlist) {
         for (Person p : q.waitingQ) {

            if (p.name.equals(patient_name)) {
               System.out.println(p.name + "님 접수 취소 성공");
               q.waitingQ.remove(p);

               break;
            }
         }
      }
   }

   static public WaitingQueue findQueueList(String doc_name) {
      for (WaitingQueue pq : Qlist) {
         if (pq.doctor_name.equals(doc_name)) {
            return pq;
         }
      }
      return null;
   }

   static public void showList() {
      for (WaitingQueue q : Qlist) {
         System.out.println(q + " : ");
         for (Person p : q.waitingQ) {
            System.out.println(p.name);
         }
      }
   }

}

class WaitingQueue {

   String doctor_name;
   Queue<Person> waitingQ;

   public WaitingQueue(String name) {
      this.doctor_name = name;
      waitingQ = new LinkedList<Person>();
   }

   public void pushToQueue(Person p) {
      waitingQ.offer(p);
   }

   public String toString() {
      return doctor_name;
   }

}

class Prescription {
   static String medicine;

   public String toString() {
      return medicine;
   }
}

class PrintPrescription {

   static public void Printing() {
      try {
         for (int i = 0; i < 5; i++) {
            System.out.println("Printing...");
            TimeUnit.SECONDS.sleep(1);
         }
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
}



class User_management {
   ArrayList<Hospital_manager> h_managers;
   Hospital_manager temp_hos;
   Scanner in = new Scanner(System.in);

   public User_management(ArrayList<Hospital_manager> h_managers) {
      this.h_managers = h_managers;
   }

   public void add() {// 병원 관리자 추가
      String h_ID;
      String h_Password;

      System.out.println("추가할 관리자 ID를 입력하세요 : ");
      h_ID = in.nextLine();
      System.out.println("추가할 관리자 비밀번호를 입력하세요 : ");
      h_Password = in.nextLine();

      h_managers.add(new Hospital_manager(h_ID, h_Password));

      System.out.println("관리자 추가 처리 완료");

   }

   public void search() {// 병원 관리자 조회

      for (Hospital_manager temp2 : h_managers) {
         System.out.println("ID: " + temp2.getID());
         System.out.println("PW: " + temp2.getPassword());
         System.out.println("------------------------------");
      }

   }

   public void modify() {// 병원 관리자 수정

      System.out.println("수정할 관리자를 선택하세요 :(ID입력) ");
      String select = in.nextLine();
      for (Hospital_manager temp2 : h_managers) {
         if (temp2.getID().equals(select)) {
            System.out.println("수정할 ID를 입력하세요 : ");
            String select_ID = in.nextLine();
            System.out.println("수정할 PW를 입력하세요 : ");
            String select_PW = in.nextLine();
            temp2.setID(select_ID);
            temp2.setPassword(select_PW);
            break;
         }
      }

      System.out.println("관리자 수정 처리 완료");

   }

   public void delete() {// 병원 관리자 삭제

      System.out.println("삭제할 관리자를 선택하세요 :(ID입력) ");
      String select = in.nextLine();
      for (Hospital_manager temp2 : h_managers) {
         if (temp2.getID().equals(select)) {
            h_managers.remove(temp2);
            break;
         }

      }

      System.out.println("관리자 삭제 처리 완료");

   }
}



/////////////////////////////////////////////////////////////////

interface PayAdapter{
	public void pay();
}

class ReservationSystem{// outer
	 

	   static public ReservationInformation getReservation(String num) throws IOException {
	      // file io
	      // if(num is true)
	      String[] ParsedOneline = null;
	      BufferedReader br = new BufferedReader(new FileReader("ReservationDB.txt"));
	      while (true) {
	         String line = br.readLine();

	         if (line == null)
	            break;
	         ParsedOneline = line.split(" ");

	         if (ParsedOneline[0].equals(num)) {
	           //return new Reservation(ParsedOneline[0], ParsedOneline[1], ParsedOneline[2], ParsedOneline[3]);// fix me
	         }
	      }

	      br.close();

	      return null;

	   }
	}

class TREATMENT{//outer system 진료시스템
	   //fix me

	   public static boolean pushReceipttoQueue(Reception reception) {
	   //waitingQueueList.findQueueList(doctor_name).pushToQueue(temp_person);
	      return true;
	   }
	   
	}

class Register{
	Payment p;
	Reception r;
	
	public void makeNewPayment() {
		p = new Payment();
	}
	
	public void enterPatiemtInformation(String name, String s_num, String addr) {
		p.setTempPatientInfo(name, s_num, addr);
		
	}
	public PatientInformation confirmPatientInformation() {
		try {
			return p.setPatientInformation();
		
		}
		catch(Exception e) {
			
		}
		return null;
		
	}
	public boolean makePayment(int amount) {
		return p.makePayment(amount);
	}
	
	public void makeNewReservationReceipt() {
		r = new Reception();
	}
	
	public ReservationInformation confirmReservationInformation() {
		return r.confirmReservationInformation();
	}
	
	public boolean requestReceipt(){
		return TREATMENT.pushReceipttoQueue(r);
	}
	
	public String enterReservationNumber(String r_num) {
		return r.enterNumber(r_num);
	}
	
	public ReservationInformation confirmReservationNumber() {
		return r.setReservation();
	}
}


class PatientInformation{
	String name;
	String securityNumber;
	String addr;
	
	ArrayList<TreatmentRecord> tr = new ArrayList<TreatmentRecord>();

	
}

class DoctorInformation{
	ArrayList<Date> workingDate = new ArrayList<Date>();
	String name;
	
}


class ReservationInformation{
	DoctorInformation di;
	PatientInformation pi;
	String reservationNumber;
	Date date; // new Date();
	LocalTime time; // LocalTime.new()
}

class Reception{
	ReservationInformation ri;
	boolean isComplete  ;
	   
	public String enterNumber(String rNum) {
		
		ri.reservationNumber = rNum;
		
		return ri.reservationNumber;
	}
	
	public ReservationInformation setReservation() {
		//외부에서 정보 가져오기 
		try {
			ReservationSystem.getReservation(ri.reservationNumber);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		return ri;
	}
	
	public ReservationInformation confirmReservationInformation() {
		return ri;
	}
}

class TreatmentRecord{
	
	   Money fee  = new Money();
	   
	   public TreatmentRecord(String num)
	   {
		   fee.amount = Integer.parseInt(num);
	   }
	}

class Money{
	   int amount;
	   String unit;
	}

class Payment {

		Money amount;
		boolean paymentComplete;
		PatientInformation pi;
		PayAdapter pa;

		
		public PatientInformation setTempPatientInfo(String name, String s_num, String addr) {
			pi = new PatientInformation();
			
			pi.name = name;
			pi.securityNumber = s_num;
			pi.addr = addr;
			
			return pi;
		}
		
		  public PatientInformation setPatientInformation() throws IOException {
		      // file(patient db) io
			  PatientInformation p =  EMR.getPatientInformation(pi);
			  
			  return p;

		   }
		
		
		public boolean makePayment(int amount) {
			//외부랑 연결
			pa = new OuterPaySystem_1(); // 1번 시스템과 연결 
			
			pa.pay();
			return true;
		}
}


class OuterPaySystem_1 implements PayAdapter{
	
	@Override
	public void pay() {
		// TODO Auto-generated method stub
		  try {
		         System.out.println("카드를 넣어 주세요. \n");
		         TimeUnit.SECONDS.sleep(1);

		         for (int i = 0; i < 3; i++) {
		            System.out.println("Processing...");
		            TimeUnit.SECONDS.sleep(1);
		         }
		         System.out.println("카드 결제 성공. \n");

		      } catch (Exception e) {
		         System.out.println(e.getMessage());
		         //return false;
		      }
	}

	}
class admin{
	protected String ID;
	protected String Password;
	
}
class System_manager {

	   private String ID;
	   private String Password;

	   public System_manager(String ID, String Password) {
	      this.ID = ID;
	      this.Password = Password;
	   }

	}


class Hospital_manager {

   private String ID;
   private String Password;

   public Hospital_manager(String ID, String Password) {
      this.ID = ID;
      this.Password = Password;
   }

}

class Manager {
	
	ArrayList<System_manager> s_manager = null;
	ArrayList<Hospital_manager> h_manager = null;
	
	public Manager() {
		
		try {
			ArrayList<Hospital_manager> h_manager = new ArrayList<Hospital_manager>();
			read_h_manager();
			
			ArrayList<System_manager> s_manager = new ArrayList<System_manager>();
			read_s_manager();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	  public void read_h_manager() throws IOException {
	      // file(patient db) io
		  
	      String[] ParsedOneline = null;
	      BufferedReader br = new BufferedReader(new FileReader("h_manager.txt"));
	      while (true) {
	         String line = br.readLine();

	         if (line == null)
	            break;
	         ParsedOneline = line.split(" ");
	         
	         Hospital_manager hm = new Hospital_manager(ParsedOneline[0], ParsedOneline[1]);
	         
	         put_h_manager(hm);
	      }
	      
	      br.close();

	   }

	  public void read_s_manager() throws IOException {
	      // file(patient db) io
		  
	      String[] ParsedOneline = null;
	      BufferedReader br = new BufferedReader(new FileReader("s_manager.txt"));
	      while (true) {
	         String line = br.readLine();

	         if (line == null)
	            break;
	         ParsedOneline = line.split(" ");
	         
	         System_manager sm = new System_manager(ParsedOneline[0], ParsedOneline[1]);
	         
	         put_s_manager(sm);
	      }
	      
	      br.close();
	      
	   }

	public void put_h_manager(Hospital_manager HosM){
		h_manager.add(HosM);
	}
	
	public void put_s_manager(System_manager SysM) {
		s_manager.add(SysM);
	}
	
}
/////////////main/////////////
public class Hospital {

   static Scanner in;

   public static void main(String[] args) throws IOException {

      Manager manager = new Manager();
      Register register = new Register();
      
      
      while (true) {

         String opt;
         Person temp_person = null;
         String name;
         String security_number;// 주민번호

         System.out.println("Please select the menu.");
         System.out.println("--------------------------------");
       
         System.out.println("1. 예약 환자 접수");
         System.out.println("2. 진료비 결제");
       

         int menu = in.nextInt();
         in.nextLine();

         if (menu == 1) {// 예약환자접수(reservation);

        	register.makeNewReservationReceipt();
        	
            String reservation_number;
            ReservationInformation rii ;
            
            do {
               System.out.println("예약 번호 입력 : ");
               reservation_number = in.nextLine();
               reservation_number = register.enterReservationNumber(reservation_number);
             
               System.out.println("\n" + reservation_number + "\n" + "정보가 맞습니까?(y,n) : ");
    
               opt = in.nextLine();

            } while (opt.equals("n"));

           rii = register.confirmReservationNumber();

            
            if (rii != null) {
               System.out.println("이름 : " + rii.pi.name);
               System.out.println("주민번호 : " + rii.pi.securityNumber);
               System.out.println("담당 의사 : " + rii.di.name);
               System.out.println("해당 예약 정보가 맞습니까?(y,n) : ");
               opt = in.nextLine();
               
               if (opt.equals("y")) {
                  
                  register.confirmReservationInformation();
                  if(register.requestReceipt())
                	  System.out.println("접수 완료\n");
                  else
                	  System.out.println("접수 실패 \n");
                  
               } else {
                  System.out.println("관리자에게 문의 하세요 \n");
               }
            } else {
               System.out.println("해당 예약 정보가 없습니다. \n");
            }

         } else if (menu == 2) {
            // 진료비 결제

        	 register.makeNewPayment();
        	 
             do {
                System.out.println("이름을 입력하세요 : ");
                name = in.nextLine();
                System.out.println("주민번호를 입력하세요 : ");
                security_number = in.nextLine();
                register.enterPatiemtInformation(name, security_number, "");
                System.out.println("\n" + name + " " + security_number + "\n" + "정보가 맞습니까?(y,n) : ");
                opt = in.nextLine();

                if (opt.equals("y")) {
                	
                	PatientInformation tp = register.confirmPatientInformation();
                  
                  if (tp != null)
                  {
                	 Iterator<TreatmentRecord> it = tp.tr.iterator();
                	 while(it.hasNext()) {
                		 System.out.println(it.next());
                		 
                		 System.out.println("액수를 입력하세요 : ");
                		 int amount = in.nextInt();
                         in.nextLine();
                         
                         if(register.makePayment(amount)) {
                        	 System.out.println("결제완료 ");
                         }
                         else {
                        	System.out.println("실패 ");
                         }
                	 }
                	
                	  break;
                  }
                  else {
                     System.out.println("등록된 정보가 없습니다. \n");
                     opt = "n";
                  }
               }
            } while (opt.equals("n"));
         }
}
   }
}