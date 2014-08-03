package cn.sdp.pkv.client.checkin;



public class CheckinMain {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		PKVClient client = new PKVClient(0, 100000); 
		client.run();

//		int sum = 0;
//		for (int k = 21;k < 40;k++)
//		{
//			int s = 000000;
//			int e = s+200000;
//			while (e <= 20000000)
//			{
//				PKVClient client = new PKVClient(s, e);
//				if (client.run())
//				{
//					break;
//				}
//				Thread.sleep(40000);
//				s += 200000;
//				e += 200000; 
//			}
//		}
//		System.out.println(sum);
	}
}
