package renderEngine;

public class RawModel {

	
		private int _vaoID;
		private int _vertexCount;
		
		public RawModel(int vaoID, int vertexCount)
		{
			_vaoID = vaoID;
			_vertexCount = vertexCount;
		}
		
		public int getVaoId()
		{
			return _vaoID;
		}
		
		public int getVertexCount()
		{
			return _vertexCount;
		}
}
