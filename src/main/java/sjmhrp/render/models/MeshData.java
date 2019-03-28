package sjmhrp.render.models;

import java.io.Serializable;

public class MeshData implements Serializable{
	
	private static final long serialVersionUID = 6123086145398086517L;
	
	int[] indices;
	int indexVbo;
	double[] vertices;
	int vertexVbo;
	double[] uvs;
	int uvVbo;
	double[] normals;
	int normalVbo;
	int[] jointIDs;
	int jointIDVbo;
	double[] weights;
	int weightsVbo;
	double furthest;
	int materialVbo;
	double[] materials;
	
	public MeshData(double[] vertices, int[] indices, double[] normals, double[] materials) {
		this.vertices=vertices;
		this.indices=indices;
		this.normals=normals;
		this.materials=materials;
	}
	
	public MeshData(double[] vertices, int[] indices, double[] normals) {
		this.vertices=vertices;
		this.indices=indices;
		this.normals=normals;
	}
	
	public MeshData(double[] vertices, int vertexVbo) {
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] uvs, int uvVbo) {
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.uvs=uvs;
		this.uvVbo=uvVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] normals, int normalVbo, int[] indices, int indexVbo) {
		this.indices=indices;
		this.indexVbo=indexVbo;
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.normals=normals;
		this.normalVbo=normalVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] normals, int normalVbo, int[] indices, int indexVbo, double[] materials, int materialVbo) {
		this.indices=indices;
		this.indexVbo=indexVbo;
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.normals=normals;
		this.normalVbo=normalVbo;
		this.materials=materials;
		this.materialVbo=materialVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] uvs, int uvVbo, double[] normals, int normalVbo, int[] indices, int indexVbo) {
		this.indices=indices;
		this.indexVbo=indexVbo;
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.uvs=uvs;
		this.uvVbo=uvVbo;
		this.normals=normals;
		this.normalVbo=normalVbo;
	}
	
	public MeshData(double[] vertices, double[] uvs, double[] normals, int[] indices, int[] jointIDs, double[] weights, double furthest) {
		this.vertices=vertices;
		this.uvs=uvs;
		this.normals=normals;
		this.indices=indices;
		this.jointIDs=jointIDs;
		this.weights=weights;
		this.furthest=furthest;
	}

	public void setIndexVbo(int indexVbo) {
		this.indexVbo = indexVbo;
	}

	public void setVertexVbo(int vertexVbo) {
		this.vertexVbo = vertexVbo;
	}

	public void setUvVbo(int uvVbo) {
		this.uvVbo = uvVbo;
	}

	public void setNormalVbo(int normalVbo) {
		this.normalVbo = normalVbo;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getIndexVbo() {
		return indexVbo;
	}

	public double[] getVertices() {
		return vertices;
	}

	public int getVertexVbo() {
		return vertexVbo;
	}

	public double[] getUvs() {
		return uvs;
	}

	public int getUvVbo() {
		return uvVbo;
	}

	public double[] getNormals() {
		return normals;
	}

	public int getNormalVbo() {
		return normalVbo;
	}
	
	public int[] getJointIDs() {
		return jointIDs;
	}
	
	public int getJointIDVbo() {
		return jointIDVbo;
	}
	
	public double[] getWeights() {
		return weights;
	}
	
	public int getWeightsVbo() {
		return weightsVbo;
	}
	
	public double getFurthest() {
		return furthest;
	}
	
	public double[] getMaterials() {
		return materials;
	}
	
	public int getMaterialVbo() {
		return materialVbo;
	}
}